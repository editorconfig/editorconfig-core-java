package org.editorconfig.core.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileStreamProvider implements StreamProvider {
  @Override
  public String getParent(String filePath) {
    return new File(filePath).getParent();
  }

  @Override
  public String combinePath(String dirPath, String filePath) {
    return new File(dirPath, filePath).getPath();
  }

  @Override
  public InputStream openStream(String filePath) {
    try{
      if (new File(filePath).exists()){
        return new FileInputStream(filePath);
      }
      else {
        return null;
      }
    }
    catch (FileNotFoundException ex){
      return null;
    }
  }
}