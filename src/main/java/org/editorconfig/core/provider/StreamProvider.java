package org.editorconfig.core.provider;

import java.io.InputStream;

public interface StreamProvider {
  /**
   * open InputStream for specified path
   * @param filePath file path to open file.
   * @return opened InputStream
   */
  InputStream openStream(String filePath);
}
