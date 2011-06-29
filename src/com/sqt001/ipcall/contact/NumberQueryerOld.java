package com.sqt001.ipcall.contact;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.Contacts.PhonesColumns;

class NumberQueryerOld extends NumberQueryer {
  public NumberQueryerOld(Cursor cursor, Context context) {
    super(cursor, context);
  }

  @Override
  protected long onQueryContactId() {
    return cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
  }

  @Override
  protected void onQuery() {
    queryMobile();
    queryHome();
    queryWork();
  }

  private void queryMobile() {
    add(query(PhonesColumns.TYPE_MOBILE));
  }

  private void queryHome() {
    add(query(PhonesColumns.TYPE_HOME));
  }

  private void queryWork() {
    add(query(PhonesColumns.TYPE_WORK));
  }

  private String query(int type) {
    String number = null;
    Cursor c = context.getContentResolver().query(Contacts.Phones.CONTENT_URI, new String[] { PhonesColumns.NUMBER },
        "type=" + type + " AND person=" + contactId, null, null);
    if (c.moveToNext()) {
      try {
        number = c.getString(c.getColumnIndexOrThrow(PhonesColumns.NUMBER));
      } catch (IllegalArgumentException e) {
        number = BLANK_NUMBER;
      }
    }
    return number;
  }
}
