package com.sqt001.ipcall.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * 与SDCar操作有关的工具类
 */
public class SDCarUtil {

  public static String SDPATH = Environment.getExternalStorageDirectory() + "/";

  private SDCarUtil() {
  }

  public static SDCarUtil getInstance() {
    return new SDCarUtil();
  }

  public String getSDPATH() {
    return SDPATH;
  }

  public void setSDPATH(String sDPATH) {
    SDPATH = sDPATH;
  }

  /**
   * 创建文件
   * @param fileName
   * @return file
   */
  public File createSDFile(String fileName) {
    File file = new File(SDPATH + fileName);
    try {
      file.createNewFile();
    } catch (IOException e) {
      Log.d("hcl", "文件创建失败");
      e.printStackTrace();
    }
    return file;
  }

  /**
   * 创建目录
   * @param dirName
   * @return dir
   */
  public File createSDDir(String dirName) {
    File dir = new File(SDPATH + dirName);
    dir.mkdir();
    return dir;
  }

  /**
   * 判断文件是否存在
   * @param fileName
   * @return true file exists,false file unexists
   */
  public boolean isFileExist(String fileName) {
    File file = new File(fileName);
    return file.exists();
  }

  /**
   * 流的形式写入SDCar
   * @param path
   * @param fileName
   * @param input
   * @return file
   */
  public File writeToSDFromInput(String path, String fileName, InputStream input) {
    File file = null;
    OutputStream out = null;
    createSDDir(path);
    file = createSDFile(fileName);
    try {
      out = new FileOutputStream(file);
      byte[] buffer = new byte[4 * 1024];
      while ((input.read(buffer)) != -1) {
        out.write(buffer);
      }
      out.flush();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return file;
  }

  /**
   * 将内容给写入sd卡
   * @param path
   * @param value
   */
  public void writeToSdCard(String path, String value) {
    w(path, value, true);
  }

  public void w(String path, String value, boolean append) {
    if (path == null || path.length() <= 0) {
      return;
    }
    if (value == null || value.length() <= 0) {
      return;
    }
    FileWriter writer = null;
    try {
      File file = new File(path);
      if (!file.exists()) {
        file.createNewFile();
      }
      writer = new FileWriter(file, append);
      writer.write(value);
    } catch (Exception e) {
      // do nothing.
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          // do nothing.
        }
      }
    }
  }

  /**
   * 从指定路径文件读出文件中的内容
   * @param path
   * @return result
   */
  public String readToSdCard(String path) {
    if (path == null || path.length() <= 0) {
      return "";
    }
    File f = new File(path);
    if (f.isDirectory()) {
      return "";
    }
    FileReader reader = null;
    String result = "";
    try {
      File file = new File(path);
      if (!file.exists()) {
        file.createNewFile();
      }
      reader = new FileReader(file);
      int length = (int) file.length();
      char[] temp = new char[length];
      reader.read(temp);
      result = new String(temp);
    } catch (Exception e) {
      // do nothing.
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          // do nothing.
        }
      }
    }
    return result;
  }
}
