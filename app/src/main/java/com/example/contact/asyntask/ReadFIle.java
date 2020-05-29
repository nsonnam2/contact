package com.example.contact.asyntask;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.example.contact.model.Contact;

import java.io.InputStream;
import java.util.ArrayList;

public class ReadFIle extends AsyncTask<Context, Integer, ArrayList<Contact>> {

    private CallBack callBack;

    public ReadFIle(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        callBack.onPreExecute();
    }

    @Override
    protected ArrayList<Contact> doInBackground(Context... contexts) {
        return getContactList(contexts[0]);
    }

    public static ArrayList<Contact> getContactList(Context context) {
        ArrayList<Contact> list = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }

                    while (cursorInfo.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Contact contact = new Contact(id, name,0, false);

                        list.add(contact);
                    }

                    cursorInfo.close();
                }
            }
            cursor.close();
        }
        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> list) {
        callBack.onPostExecute(list);
    }

    public interface CallBack {
        void onPreExecute();

        void onPostExecute(ArrayList<Contact> list);
    }
}
