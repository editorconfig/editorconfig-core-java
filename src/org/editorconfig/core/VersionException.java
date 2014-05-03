package org.editorconfig.core;

/**
 * @author Dennis.Ushakov
 */
public class VersionException extends EditorConfigException {
  public VersionException(String s) {
    super(s, null);
  }
}
