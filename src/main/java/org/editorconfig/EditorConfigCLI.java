package org.editorconfig;

import org.editorconfig.core.EditorConfig;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis.Ushakov
 */
public class EditorConfigCLI {
  public static void main(String[] args) throws Exception {
    List<String> filePaths = new ArrayList<String>();
    String configFilename = null;
    String version = EditorConfig.VERSION;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if ("-v".equals(arg) || "--version".equals(arg)) {
        System.out.println("EditorConfig Java Version " + version);
        System.exit(0);
      }
      if ("-h".equals(arg) || "--help".equals(arg)) {
        printUsage(false);
      }
      if ("-b".equals(arg)) {
        if (i + 1 < args.length) {
          version = args[++i];
          continue;
        } else {
          printUsage(true);
        }
      }
      if ("-f".equals(arg)) {
        if (i + 1 < args.length) {
          configFilename = args[++i];
          continue;
        } else {
          printUsage(true);
        }
      }
      filePaths.add(arg);
    }
    if (filePaths.isEmpty()) {
      printUsage(true);
    }
    for (String filePath : filePaths) {
      List<EditorConfig.OutPair> properties = new EditorConfig(configFilename, version).getProperties(filePath);
      if (filePaths.size() > 1) {
        System.out.println("[" + filePath + "]");
      }
      for (EditorConfig.OutPair property : properties) {
        System.out.println(property.getKey() + "=" + property.getVal());
      }
    }
  }

  private static void printUsage(boolean error) {
    PrintStream out = error ? System.err : System.out;
    out.println("[OPTIONS] filename");
    out.println("-f                 Specify conf filename other than \".editorconfig\".");
    out.println("-b                 Specify version (used by devs to test compatibility).");
    out.println("-h OR --help       Print this help message.");
    out.println("-v OR --version    Display version information.");
    System.exit(error ? 2 : 0);
  }
}
