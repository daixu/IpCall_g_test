package com.sqt001.ipcall.util;

public class SoftObj {
  public SoftObj(String id, String title, String message, String url, int fileSize, int downloadSize, int isDownload) {
    this.setId(id);
    this.setTitle(title);
    this.setMessage(message);
    this.setUrl(url);
    this.setFileSize(fileSize);
    this.setDownloadSize(downloadSize);
    this.setIsDownload(isDownload);
  }

  private String id = "";
  private String title = "";
  private String message = "";
  private String url = "";
  private int fileSize = 0;
  private int downloadSize = 0;
  private int isDownload = 0;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setFileSize(int fileSize) {
    this.fileSize = fileSize;
  }

  public int getFileSize() {
    return fileSize;
  }

  public void setDownloadSize(int downloadSize) {
    this.downloadSize = downloadSize;
  }

  public int getDownloadSize() {
    return downloadSize;
  }

  public void setIsDownload(int isDownload) {
    this.isDownload = isDownload;
  }

  public int getIsDownload() {
    return isDownload;
  }

}
