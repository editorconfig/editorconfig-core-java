package org.editorconfig.core;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dennis.Ushakov
 */
public class EditorConfig {
  public static String VERSION = "0.11.3-final";

  private static final Pattern SECTION_PATTERN = Pattern.compile("\\s*\\[(([^#;]|\\\\#|\\\\;)+)]" +
                                                                 ".*"); // Python match searches from the line start
  private static final int HEADER = 1;

  private static final Pattern OPTION_PATTERN = Pattern.compile("\\s*([^:=\\s][^:=]*)\\s*[:=]\\s*(.*)");
  private static final int OPTION = 1;
  private static final int VAL = 2;

  private final String configFilename;
  private final String version;

  public EditorConfig() {
    this(".editorconfig", VERSION);
  }

  public EditorConfig(String configFilename, String version) {
    this.configFilename = configFilename;
    this.version = version;
  }

  public List<OutPair> getProperties(String filePath) throws EditorConfigException {
    checkAssertions();
    Map<String, String> oldOptions = Collections.emptyMap();
    Map<String, String> options = new LinkedHashMap<String, String>();
    try {
      boolean root = false;
      String dir = new File(filePath).getParent();
      while (dir != null && !root) {
        String configPath = dir + "/" + configFilename;
        if (new File(configPath).exists()) {
          FileInputStream stream = new FileInputStream(configPath);
          InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
          BufferedReader bufferedReader = new BufferedReader(reader);
          try {
            root = parseFile(bufferedReader, dir + "/", filePath, options);
          } finally {
            bufferedReader.close();
            reader.close();
            stream.close();
          }
        }
        options.putAll(oldOptions);
        oldOptions = options;
        options = new LinkedHashMap<String, String>();
        dir = new File(dir).getParent();
      }
    } catch (IOException e) {
      throw new EditorConfigException(null, e);
    }

    preprocessOptions(oldOptions);

    final List<OutPair> result = new ArrayList<OutPair>();
    for (Map.Entry<String, String> keyValue : oldOptions.entrySet()) {
      result.add(new OutPair(keyValue.getKey(), keyValue.getValue()));
    }
    return result;
  }

  private void checkAssertions() throws VersionException {
    if (compareVersions(version, VERSION) > 0) {
      throw new VersionException("Required version is greater than the current version.");
    }
  }

  private int compareVersions(String version1, String version2) {
    String[] version1Components = version1.split("(\\.|-)");
    String[] version2Components = version2.split("(\\.|-)");
    for (int i = 0; i < 3; i++) {
      String version1Component = version1Components[i];
      String version2Component = version2Components[i];
      int v1 = -1;
      int v2 = -1;
      try {
        v1 = Integer.parseInt(version1Component);
      } catch (NumberFormatException ignored) {}
      try {
        v2 = Integer.parseInt(version2Component);
      } catch (NumberFormatException ignored) {}
      if (v1 != v2) return v1 - v2;
    }
    return 0;
  }

  private void preprocessOptions(Map<String, String> options) {
    // Lowercase option value for certain options
    for (String key : new String[]{"end_of_line", "indent_style", "indent_size", "insert_final_newline",
                                   "trim_trailing_whitespace", "charset"}) {
      String value = options.get(key);
      if (value != null) {
        options.put(key, value.toLowerCase(Locale.US));
      }
    }

    // Set indent_size to "tab" if indent_size is unspecified and
    // indent_style is set to "tab".
    if ("tab".equals(options.get("indent_style")) && !options.containsKey("indent_size") &&
        compareVersions(version, "0.10.0") >= 0) {
      options.put("indent_size", "tab");
    }

    // Set tab_width to indent_size if indent_size is specified and
    // tab_width is unspecified
    String indent_size = options.get("indent_size");
    if (indent_size != null && !"tab".equals(indent_size) && !options.containsKey("tab_width")) {
      options.put("tab_width", indent_size);
    }

    // Set indent_size to tab_width if indent_size is "tab"
    String tab_width = options.get("tab_width");
    if ("tab".equals(indent_size) && tab_width != null) {
      options.put("indent_size", tab_width);
    }
  }

  private boolean parseFile(BufferedReader bufferedReader, String dirName, String filePath, Map<String, String> result) throws IOException, EditorConfigException {
    final StringBuilder malformedLines = new StringBuilder();
    boolean root = false;
    boolean inSection = false;
    boolean matchingSection = false;
    while (bufferedReader.ready()) {
      String line = bufferedReader.readLine().trim();

      if (line.startsWith("\ufeff")) {
        line = line.substring(1);
      }

      // comment or blank line?
      if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) continue;

      Matcher matcher = SECTION_PATTERN.matcher(line);
      if (matcher.matches()) {
        inSection = true;
        matchingSection = filenameMatches(dirName, matcher.group(HEADER), filePath);
        continue;
      }
      matcher = OPTION_PATTERN.matcher(line);
      if (matcher.matches()) {
        String key = matcher.group(OPTION).trim().toLowerCase(Locale.US);
        String value = matcher.group(VAL);
        value = value.equals("\"\"") ? "" : value;
        if (!inSection && "root".equals(key)) {
          root = true;
        } else if (matchingSection && !result.containsKey(key)) {
          int commentPos = value.indexOf(" ;");
          commentPos = commentPos < 0 ? value.indexOf(" #") : commentPos;
          value = commentPos >= 0 ? value.substring(0, commentPos) : value;
          result.put(key, value);
        }
        continue;
      }
      malformedLines.append(line).append("\n");
    }
    if (malformedLines.length() > 0) {
      throw new EditorConfigException(malformedLines.toString(), null);
    }
    return root;
  }

  private boolean filenameMatches(String configDirname, String pattern, String filePath) {
    pattern = pattern.replace(File.separatorChar, '/');
    pattern = pattern.replaceAll("\\\\#", "#");
    pattern = pattern.replaceAll("\\\\;", ";");
    int separator = pattern.indexOf("/");
    if (separator >= 0) {
      pattern = configDirname + (separator == 0 ? pattern.substring(1) : pattern);
    } else {
      pattern = "**/" + pattern;
    }
    return Pattern.compile(convertGlobToRegEx(pattern)).matcher(filePath).matches();
  }

  private String convertGlobToRegEx(String pattern) {
    int length = pattern.length();
    StringBuilder result = new StringBuilder(length);
    int i = 0;
    boolean escaped = false;
    while (i < length) {
      char current = pattern.charAt(i);
      i++;
      if ('*' == current) {
        if (i < length && pattern.charAt(i) == '*') {
          result.append(".*");
          i++;
        } else {
          result.append("[^/]*");
        }
      } else if ('?' == current) {
        result.append(".");
      } else if ('[' == current) {
        int j = i;
        if (j < length && pattern.charAt(j) == '!') {
          j++;
        }
        if (j < length && pattern.charAt(j) == ']') {
          j++;
        }
        while (j < length && (pattern.charAt(j) != ']' || escaped)) {
          escaped = pattern.charAt(j) == '\\' && !escaped;
          j++;
        }
        if (j >= length) {
          result.append("\\[");
        } else {
          String charClass = pattern.substring(i, j);
          i = j + 1;
          if (charClass.charAt(0) == '!') {
            charClass = '^' + charClass;
          } else if (charClass.charAt(0) == '^') {
            charClass = "\\" + charClass;
          }
          result.append('[').append(charClass).append("]");
        }
      } else if ('{' == current) {
        int j = i;
        final List<String> groups = new ArrayList<String>();
        while (j < length && pattern.charAt(j) != '}') {
          int k = j;
          while (k < length && (",}".indexOf(pattern.charAt(k)) < 0 || escaped)) {
            escaped = pattern.charAt(k) == '\\' && !escaped;
            k++;
          }
          String group = pattern.substring(j, k);
          for (char c : new char[] {',', '}', '\\'}){
            group = group.replace("\\" + c, String.valueOf(c));
          }
          groups.add(group);
          j = k;
          if (j < length && pattern.charAt(j) == ',') {
            j++;
            if (j < length && pattern.charAt(j) == '}') {
              groups.add("");
            }
          }
        }
        if (j >= length || groups.size() < 2) {
          result.append("\\{");
        } else {
          result.append("(");
          for (int groupNumber = 0; groupNumber < groups.size(); groupNumber++) {
            String group = groups.get(groupNumber);
            if (groupNumber > 0) result.append("|");
            result.append(escapeToRegex(group));
          }
          result.append(")");
          i = j + 1;
        }
      } else {
        result.append(escapeToRegex(String.valueOf(current)));
      }
    }

    return result.toString();
  }

  private String escapeToRegex(String group) {
    final StringBuilder builder = new StringBuilder(group.length());
    for (char c : group.toCharArray()) {
      if (c == ' ' || Character.isLetter(c) || Character.isDigit(c) || c == '_') {
        builder.append(c);
      } else if (c == '\n') {
        builder.append("\\n");
      } else {
        builder.append("\\").append(c);
      }
    }
    return builder.toString();
  }

  public static class OutPair {
    private final String key;
    private final String val;

    public OutPair(String key, String val) {
      this.key = key;
      this.val = val;
    }

    public String getKey(){
      return key;
    }

    public String getVal() {
      return val;
    }
  }
}
