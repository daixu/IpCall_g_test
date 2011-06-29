package com.sqt001.ipcall.contact;

import android.net.Uri;
import android.provider.Contacts;

class ContactContentUriOld extends ContactContentUri {
  @Override
  protected Uri onGetUri() {
    return Contacts.People.CONTENT_URI;
  }
}
