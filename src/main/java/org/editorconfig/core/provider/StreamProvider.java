package org.editorconfig.core.provider;

import java.io.InputStream;

public interface StreamProvider {
  /**
   * get Parent directory
   * @param filePath
   * @return parent direcoty path by String
   */
  String getParent(String filePath);

  /**
   * combine Path
   * @param dirPath directory path
   * @param filePath file path
   * @return dirPath + filePath
   */
  String combinePath(String dirPath, String filePath);

  /**
   * open InputStream for specified path
   * @param filePath file path to open file.
   * @return opened InputStream
   */
  InputStream openStream(String filePath);
}
