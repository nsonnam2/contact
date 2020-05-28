package com.example.contact.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.contact.activity.EmojiActivity;
import com.example.contact.model.Contact;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = "Utils";

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
                        Contact contact = new Contact(id, name, 0, false);

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

    public static ArrayList<Contact> sort(ArrayList<Contact> listContacts) {
        if (listContacts.size() > 0){
            Collections.sort(listContacts, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

            Contact contact = new Contact("", listContacts.get(0).getName().substring(0, 1), 1 ,false);
            listContacts.add(0, contact);

            for (int i = 0; i < listContacts.size(); i++) {

                if (listContacts.indexOf(listContacts.get(i)) == listContacts.size() - 1) {
                    if (!listContacts.get(i).getName().substring(0, 1).equals(listContacts.get(i - 1).getName().substring(0, 1))) {
                        Contact contact1 = new Contact("", listContacts.get(i).getName().substring(0, 1), 1, false);
                        listContacts.add(listContacts.indexOf(listContacts.get(i - 1)), contact1);
                    }
                    return listContacts;
                }

                if (!listContacts.get(i).getName().substring(0, 1).equals(listContacts.get(i + 1).getName().substring(0, 1))) {
                    Contact contact1 = new Contact("", listContacts.get(i + 1).getName().substring(0, 1), 1, false);
                    listContacts.add(listContacts.indexOf(listContacts.get(i + 1)), contact1);
                }
            }
        }

        return listContacts;
    }

    public static boolean check(String text) {
        String regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";
        Matcher matchEmo = Pattern.compile(regex).matcher(text);
        while (matchEmo.find()) {
            return true;
        }
        return false;
    }

    public static void updateContact(Context context, String id, String name) {
        try {
            ContentResolver contentResolver = context.getContentResolver();

            String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

            String[] nameParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

            ArrayList<android.content.ContentProviderOperation> ops = new ArrayList<>();

            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public static int getIndex(String text, ArrayList<Contact> list){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().toLowerCase().startsWith(text.toLowerCase()) && list.get(i).getType() == 0){
                return list.indexOf(list.get(i));
            }
        }

        return -1;
    }
}
