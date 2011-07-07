package com.sqt001.ipcall.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sqt001.ipcall.provider.IpCallHelper;

public class DBUtilxx {
  private IpCallHelper mDbHelper;

  public DBUtilxx(Context context) {
    mDbHelper = new IpCallHelper(context);
  }

  /** 读取所有Soft数据，返回ArrayList数据集群 */
  public ArrayList<SoftObj> readAll() {
    ArrayList<SoftObj> buffer = new ArrayList<SoftObj>();

    SQLiteDatabase db = null;
    Cursor cur = null;
    try {
      db = mDbHelper.getWritableDatabase();

      cur = db.query(IpCallHelper.SOFT_TABLE, null, null, null, null, null, null);

      if (cur == null) {
        return buffer;
      }
      if (cur.getCount() > 0) {
        while (cur.moveToNext()) {
          String id = cur.getString(cur.getColumnIndex(IpCallHelper.SOFT_ID));
          String title = cur.getString(cur.getColumnIndex(IpCallHelper.SOFT_TITLE));
          String message = cur.getString(cur.getColumnIndex(IpCallHelper.SOFT_MESSAGE));
          String url = cur.getString(cur.getColumnIndex(IpCallHelper.SOFT_URL));
          int fileSize = cur.getInt(cur.getColumnIndex(IpCallHelper.FILE_SIZE));
          int downloadSize = cur.getInt(cur.getColumnIndex(IpCallHelper.DOWNLOAD_SIZE));

          //buffer.add(new SoftObj(id, title, message, url, fileSize, downloadSize));
        }
      }
    } catch (Exception ex) {
      // exception...
    } finally {
      if (cur != null) {
        cur.close();
      }
      if (db != null) {
        db.close();
      }
    }
    return buffer;
  }

  public ArrayList<String> readForId() {
    ArrayList<String> buffer = new ArrayList<String>();

    SQLiteDatabase db = null;
    Cursor cur = null;
    try {
      db = mDbHelper.getWritableDatabase();

      cur = db.query(IpCallHelper.SOFT_TABLE, null, null, null, null, null, null);

      if (cur == null) {
        return buffer;
      }
      if (cur.getCount() > 0) {
        while (cur.moveToNext()) {
          String id = cur.getString(cur.getColumnIndex(IpCallHelper.SOFT_ID));
          buffer.add(id);
        }
      }
    } catch (Exception ex) {
      // ignore
    } finally {
      if (cur != null) {
        cur.close();
      }
      if (db != null) {
        db.close();
      }
    }
    return buffer;
  }

  /**
   * 根据所传入的id去查询数据库中是否存在对应的记录
   * @param id
   * @return
   */
  public boolean readFromId(String id) {
    boolean isExists = false;
    SQLiteDatabase db = null;
    Cursor cur = null;
    try {
      db = mDbHelper.getWritableDatabase();
      // String sql = "select * from " + IpCallHelper.SOFT_TABLE + "where " + IpCallHelper.SOFT_ID + "=" + id;
      // cur = db.rawQuery(sql, null);
      cur = db
          .query(true, IpCallHelper.SOFT_TABLE, null, IpCallHelper.SOFT_ID + "=" + id, null, null, null, null, null);
      if (cur == null) {
        return isExists;
      }
      if (cur.getCount() > 0) {
        isExists = true;
      }
    } catch (Exception ex) {
      // ignore
    } finally {
      if (cur != null) {
        cur.close();
      }
      if (db != null) {
        db.close();
      }
    }
    return isExists;
  }

  /** 删除所有数据*/
  public boolean delAll() {
    boolean flag = false;
    SQLiteDatabase db = null;
    try {
      db = mDbHelper.getWritableDatabase();
      db.delete(IpCallHelper.SOFT_TABLE, null, null);
    } catch (Exception ex) {
      // exception...
    } finally {
      if (db != null) {
        db.close();
      }
    }
    return flag;
  }

  /**
   * 插入SoftObj(id,title,message,url)进入数据库
   * @param obj
   * @return
   */
  public boolean insertSubject(SoftObj obj) {
    boolean flag = false;
    SQLiteDatabase db = null;
    try {
      db = mDbHelper.getWritableDatabase();
      ContentValues cv = new ContentValues();
      cv.put(IpCallHelper.SOFT_ID, obj.getId());
      cv.put(IpCallHelper.SOFT_TITLE, obj.getTitle());
      cv.put(IpCallHelper.SOFT_MESSAGE, obj.getMessage());
      cv.put(IpCallHelper.SOFT_URL, obj.getUrl());
      cv.put(IpCallHelper.FILE_SIZE, obj.getFileSize());
      cv.put(IpCallHelper.DOWNLOAD_SIZE, obj.getDownloadSize());

      db.insert(IpCallHelper.SOFT_TABLE, null, cv);
      flag = true;
    } catch (Exception ex) {
      // exception...
    } finally {
      if (db != null) {
        db.close();
      }
    }
    return flag;
  }

  public void delForId(String id) {
    SQLiteDatabase db = null;
    try {
      db = mDbHelper.getWritableDatabase();
      db.delete(IpCallHelper.SOFT_TABLE, "soft_id=" + id, null);
    } catch (Exception ex) {
      // exception...
    } finally {
      if (db != null) {
        db.close();
      }
    }
  }

  public void update(String url, int fileSize, int downLoadSize) {
    SQLiteDatabase db = null;
    try {
      db = mDbHelper.getWritableDatabase();
      String sql = "updata " + IpCallHelper.SOFT_TABLE + " set " + IpCallHelper.FILE_SIZE + " = " + fileSize + " , "
          + IpCallHelper.DOWNLOAD_SIZE + " = " + downLoadSize + " where " + IpCallHelper.SOFT_URL + " = " + url;
      db.execSQL(sql, null);
      // db.update(IpCallHelper.SOFT_TABLE, null, IpCallHelper.FILE_SIZE + " = " + fileSize , null);
    } catch (Exception e) {
    }
  }
}
