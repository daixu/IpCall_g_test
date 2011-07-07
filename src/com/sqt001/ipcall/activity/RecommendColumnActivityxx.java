package com.sqt001.ipcall.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sqt001.ipcall.R;
import com.sqt001.ipcall.provider.IpCallHelper;
import com.sqt001.ipcall.util.DBUtil;
import com.sqt001.ipcall.util.SDCarUtil;
import com.sqt001.ipcall.util.SoftObj;

public class RecommendColumnActivityxx extends Activity {
  private ListView mListView;
  private int tempPosition;
  private DBUtil dbHelper;
  private List<Map<String, Object>> dataList;
  MyAdapter adapter;
  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      Map map = dataList.get(msg.what);
      ProgressBar progressBar = (ProgressBar) map.get("progressBar");
      switch (msg.arg1) {
      case 1:
        if (progressBar != null) {
          progressBar.setProgress(msg.arg2);
          progressBar.invalidate();
          if (map.get("thread") != null)
            map.put(IpCallHelper.DOWNLOAD_SIZE, msg.arg2);
        }
        break;
      case 2:
        if (progressBar != null) {
          progressBar.setProgress(100);
          progressBar.invalidate();
          map.put(IpCallHelper.DOWNLOAD_SIZE, msg.arg2);
        }
        Toast.makeText(RecommendColumnActivityxx.this, "下载完成", Toast.LENGTH_SHORT).show();
        break;
      case 3:
        Toast.makeText(RecommendColumnActivityxx.this, "下载失败", Toast.LENGTH_SHORT).show();
        break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dbHelper = new DBUtil(this);
    mListView = new ListView(this);

    if ((getData().size() > 0) && (!getData().equals(""))) {
      adapter = new MyAdapter(getData());
      adapter.notifyDataSetChanged();
      mListView.setAdapter(adapter);
      setContentView(mListView);
      mListView.setOnItemClickListener(new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> apapter, View arg1, final int position, long id) {
          final ArrayList<SoftObj> lst = new DBUtil(RecommendColumnActivityxx.this).readAll();
          new AlertDialog.Builder(RecommendColumnActivityxx.this).setTitle(lst.get(position).getTitle().trim())
              .setMessage(lst.get(position).getMessage().trim())
              .setPositiveButton(R.string.access, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  Uri uri = Uri.parse(lst.get(position).getUrl());
                  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                  startActivity(intent);
                }
              }).setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
              }).create().show();
        }
      });
    }
  }

  @Override
  protected void onResume() {
    Log.d("hcl", "onResume");
    super.onResume();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    } else {
      dataList = getData();
      if ((dataList.size() > 0) && (!dataList.equals(""))) {
        mListView = new ListView(this);
        adapter = new MyAdapter(dataList);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetInvalidated();
        setContentView(mListView);
      }
    }
  }

  class MyAdapter extends BaseAdapter {

    public MyAdapter(List<Map<String, Object>> dateList) {
      super();
      dataList = dateList;
    }

    @Override
    public int getCount() {
      return dataList.size();
    }

    @Override
    public Object getItem(int position) {
      return null;
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final Map map = dataList.get(position);
      tempPosition = position;
      final ArrayList<SoftObj> lst = new DBUtil(RecommendColumnActivityxx.this).readAll();
      if (map != null) {
        String title = (String) map.get(IpCallHelper.SOFT_TITLE);
        String message = (String) map.get(IpCallHelper.SOFT_MESSAGE);
        convertView = LayoutInflater.from(RecommendColumnActivityxx.this.getApplicationContext()).inflate(
            R.layout.recommendcolumn_item, null);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_title);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.textView_message);
        final Button btDownload = (Button) convertView.findViewById(R.id.button_download_start_or_pause);
        final Button btCancel = (Button) convertView.findViewById(R.id.button_download_cancel);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_downLoad);
        int state = 0;
        if (map.get("state") != null)
          state = (Integer) map.get("state");
        if (state == 0) {
          btDownload.setText(getString(R.string.download));
          btCancel.setVisibility(View.GONE);
          progressBar.setVisibility(View.GONE);
        }
        if (state != 1) {
          if (map.get(IpCallHelper.IS_DOWNLOAD) != null && (Integer) map.get(IpCallHelper.IS_DOWNLOAD) == 1) {
            btDownload.setText(getString(R.string.download));
            btCancel.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            if (map.get(IpCallHelper.DOWNLOAD_SIZE) != null)
              progressBar.setProgress((Integer) map.get(IpCallHelper.DOWNLOAD_SIZE));
            map.put("state", 2);
          }
        }

        btDownload.setOnClickListener(new OnClickListener() {
          int position = tempPosition;

          @Override
          public void onClick(View v) {
            int state = 0;
            if (map.get("state") != null) {
              state = (Integer) map.get("state");
            }
            if (state != 1) {
              downloadFile(lst, position);
              btDownload.setText(getString(R.string.pause));
              btCancel.setVisibility(View.VISIBLE);
              progressBar.setVisibility(View.VISIBLE);
              state = 1;
            } else if (state == 1) {
              try {
                pauseDownloadFile(position);
                btDownload.setText(getString(R.string.download));
                state = 2;
              } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(RecommendColumnActivityxx.this, "下载出错", Toast.LENGTH_LONG).show();
              }

            }
            map.put("state", state);
          }

        });
        btCancel.setOnClickListener(new OnClickListener() {
          int position = tempPosition;

          @Override
          public void onClick(View v) {
            int state = 0;
            if (map.get("state") != null) {
              state = (Integer) map.get("state");
            }
            if (state != 0) {
              cancelDownloadFile(position);
              btDownload.setText(getString(R.string.download));
              btCancel.setVisibility(View.GONE);
              progressBar.setVisibility(View.GONE);
              state = 0;
            }
            map.put("state", state);
          }
        });
        tvTitle.setText(title);
        tvMessage.setText(message);
        int progress = 0;
        if (map.get(IpCallHelper.DOWNLOAD_SIZE) != null && map.get(IpCallHelper.FILE_SIZE) != null) {
          if ((Integer) map.get(IpCallHelper.FILE_SIZE) != 0)
            progress = (Integer) map.get(IpCallHelper.DOWNLOAD_SIZE) * 100 / (Integer) map.get(IpCallHelper.FILE_SIZE);
          progressBar.setProgress(progress);
        }
        map.put("progressBar", progressBar);
      }
      return convertView;
    }

    private void cancelDownloadFile(int position) {
      Map map = dataList.get(position);
      DownloadThread thread = (DownloadThread) map.get("thread");
      int state = (Integer) map.get("state");
      Log.d("hcl", " cancelDownloadFile 1 ");
      if (thread != null) {
        if (state == 1) {
          thread.isCancel = true;
        }
        if (state == 2) {
          synchronized (thread.o) {
            thread.isCancel = true;
            thread.o.notify();
          }
        }

      } else {
        String url = (String) map.get(IpCallHelper.SOFT_URL);
        removeSDcardFileAndUpdateSQL(position, url);
      }
      Log.d("hcl", " cancelDownloadFile 2 ");
      thread = null;
      map.put(IpCallHelper.DOWNLOAD_SIZE, 0);
      map.put(IpCallHelper.FILE_SIZE, 0);
      map.put(IpCallHelper.IS_DOWNLOAD, 0);
      map.put("thread", thread);

    }

    private void pauseDownloadFile(int position) throws Exception {
      Map map = dataList.get(position);
      DownloadThread thread = (DownloadThread) map.get("thread");
      if (thread != null) {
        thread.isPause = true;
      }
    }

    private void downloadFile(final ArrayList<SoftObj> lst, int position) {
      Map map = dataList.get(position);
      Uri uri = Uri.parse(lst.get(position).getUrl());
      String strUri = uri.toString();

      if (strUri.endsWith(".apk")) {
        map.put(IpCallHelper.IS_DOWNLOAD, 1);
        dbHelper.updateForIsDownload(strUri, 1);

        DownloadThread thread = (DownloadThread) map.get("thread");
        Log.d("hcl", "thread = " + thread);
        if (thread == null) {
          thread = new DownloadThread(strUri, position);
          thread.start();
        } else {
          thread.isPause = false;
          synchronized (thread.o) {
            thread.o.notify();
          }

        }

        map.put("thread", thread);

      } else {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        return;
      }
    }
  }

  private void removeSDcardFileAndUpdateSQL(int position, String url) {
    Map map = dataList.get(position);
    dbHelper.updateForIsDownload(url, 0, 0, 0);
    map.put(IpCallHelper.DOWNLOAD_SIZE, 0);
    map.put(IpCallHelper.FILE_SIZE, 0);
    map.put(IpCallHelper.IS_DOWNLOAD, 0);
    map.put("thread", null);
  }

  class DownloadThread extends Thread {

    int position;
    int downLoadFileSize;
    int fileSize;
    String url;
    String mDirName = "newding/";
    Object o = new Object();
    boolean isPause = false;
    boolean isCancel = false;

    public DownloadThread(String url, int position) {
      super();
      this.position = position;
      this.url = url;
    }

    @Override
    public void run() {
      super.run();
      downHttpFile(url);
      if (isCancel) {
        removeSDcardFileAndUpdateSQL(position, url);
      }
    }

    private InputStream getFileInputStream(String url) {
      URL myFileUrl;
      InputStream is = null;
      HttpURLConnection conn = null;
      try {
        myFileUrl = new URL(url);

        int startPosition = getStartPosition();
        conn = (HttpURLConnection) myFileUrl.openConnection();
        downLoadFileSize = startPosition;
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Range", "bytes=" + startPosition + "-");
        conn.setConnectTimeout(3 * 1000);
        conn.setDoInput(true);
        conn.connect();
        is = conn.getInputStream();
        int i = 3;

        if (startPosition == 0) {
          fileSize = conn.getContentLength();// 根据响应获取文件大小
          saveFileInfo();
        } else {
          Map map = dataList.get(position);
          if (map.get(IpCallHelper.FILE_SIZE) != null)
            fileSize = (Integer) map.get(IpCallHelper.FILE_SIZE);
        }
        Log.d("hcl", "" + "startPosition" + startPosition);
        Log.d("hcl", "fileSize" + fileSize);
      } catch (Exception e) {
        e.printStackTrace();
        sendWrongMessage();
      }

      return is;
    }

    private void saveFileInfo() {
      Map map = dataList.get(position);
      String id = (String) map.get(IpCallHelper.SOFT_ID);
      String title = (String) map.get(IpCallHelper.SOFT_TITLE);
      String message = (String) map.get(IpCallHelper.SOFT_MESSAGE);
      String url = (String) map.get(IpCallHelper.SOFT_URL);
      int downloadSize = 0;
      if (map.get(IpCallHelper.DOWNLOAD_SIZE) != null)
        downloadSize = (Integer) map.get(IpCallHelper.DOWNLOAD_SIZE);
      int fileSize = 0;
      if (map.get(IpCallHelper.FILE_SIZE) != null)
        fileSize = (Integer) map.get(IpCallHelper.FILE_SIZE);
      int isDownload = 0;
      if (map.get(IpCallHelper.IS_DOWNLOAD) != null)
        isDownload = (Integer) map.get(IpCallHelper.IS_DOWNLOAD);
      dbHelper.updateForIsDownload(url, isDownload, fileSize, downloadSize);
    }

    private int getStartPosition() {
      Map map = dataList.get(position);
      int startPosition = 0;
      if (map.get(IpCallHelper.DOWNLOAD_SIZE) != null)
        startPosition = (Integer) map.get(IpCallHelper.DOWNLOAD_SIZE);
      downLoadFileSize = startPosition;
      return startPosition;
    }

    private FileOutputStream createSDPath(String url) throws FileNotFoundException {
      SDCarUtil sdUtil = SDCarUtil.getInstance();
      if (!sdUtil.isFileExist(mDirName)) {
        sdUtil.createSDDir(mDirName);
      }
      String newFilename = url.substring(url.lastIndexOf("/") + 1);
      newFilename = mDirName + newFilename + ".temp";

      FileOutputStream fos = new FileOutputStream(SDCarUtil.SDPATH + newFilename, true);
      return fos;
    }

    private void downHttpFile(String url2) {
      InputStream is = null;
      Message msg = null;
      try {
        is = getFileInputStream(url);
        if (is == null || fileSize <= 0) {
          Log.d("hcl", "is ==null");
          sendWrongMessage();
          return;
        }
        FileOutputStream fos = createSDPath(url);
        byte buf[] = new byte[1024];
        int progress;
        do {
          if (isCancel) {
            is.close();
            return;
          }
          if (isPause) {
            synchronized (this.o) {
              try {
                o.wait();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
          int numread = is.read(buf);
          if (numread == -1) {
            break;
          }
          downLoadFileSize += numread;
          writeFileAndSQL(fos, buf, numread, fileSize, downLoadFileSize);
          progress = (downLoadFileSize * 100) / fileSize;
          msg = sendUpdateMessage(progress);
          Log.d("hcl", "downLoadFileSize= " + downLoadFileSize);
        } while (true);
        // 通知下载完成
        msg = sendCompleteMessage();
        mHandler.sendMessage(msg);
        Log.d("hcl", "complete");
      } catch (IOException e) {
        e.printStackTrace();
        sendWrongMessage();
      } finally {
        try {
          if (is != null)
            is.close();
        } catch (IOException e) {
          e.printStackTrace();
          sendWrongMessage();
        }
      }

    }

    private Message sendUpdateMessage(int progress) {
      Message msg;
      msg = new Message();
      msg.arg1 = 1;
      msg.what = position;
      msg.arg2 = progress;
      mHandler.sendMessage(msg);
      return msg;
    }

    private Message sendCompleteMessage() {
      Message msg;
      msg = new Message();
      msg.what = position;
      msg.arg1 = 2;
      return msg;
    }

    private void sendWrongMessage() {
      Message msg;
      msg = new Message();
      msg.arg1 = 3;
      msg.what = position;
      mHandler.sendMessage(msg);
    }

    private void writeFileAndSQL(FileOutputStream fos, byte[] buf, int numread, int fileSize, int downLoadSize)
        throws IOException {
      fos.write(buf, 0, numread);
      dbHelper.update(url, fileSize, downLoadSize);
    }
  }

  private List<Map<String, Object>> getData() {
    ArrayList<Map<String, Object>> table = new ArrayList<Map<String, Object>>();
    ArrayList<SoftObj> lst = new DBUtil(RecommendColumnActivityxx.this).readAll();
    SoftObj so = null;
    for (int i = 0; i < lst.size(); i++) {
      so = lst.get(i);
      Map<String, Object> item = new HashMap<String, Object>();
      item.put(IpCallHelper.SOFT_ID, so.getId());
      item.put(IpCallHelper.SOFT_URL, so.getUrl());
      item.put(IpCallHelper.SOFT_TITLE, so.getTitle());
      item.put(IpCallHelper.SOFT_MESSAGE, so.getMessage());
      item.put(IpCallHelper.FILE_SIZE, so.getFileSize());
      item.put(IpCallHelper.DOWNLOAD_SIZE, so.getDownloadSize());
      item.put(IpCallHelper.IS_DOWNLOAD, so.getIsDownload());
      table.add(item);
    }
    return table;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      new AlertDialog.Builder(RecommendColumnActivityxx.this).setTitle(R.string.exit)
          .setIcon(android.R.drawable.ic_menu_info_details).setMessage(R.string.really_exit)
          .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
              System.exit(0);
            }
          }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }).create().show();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
}
