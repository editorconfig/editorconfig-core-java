package org.editorconfig.core;

import java.io.IOException;

/**
 * @author Dennis.Ushakov
 */
public class EditorConfigException extends Exception {
  public EditorConfigException(String s, IOException e) {
    super(s, e);
  }
}
