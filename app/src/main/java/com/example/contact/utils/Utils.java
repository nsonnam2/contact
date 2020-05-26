package com.example.contact.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.contact.model.Contact;

import java.util.ArrayList;
import java.util.Collections;

public class Utils {

    private static final String TAG = "Utils";

    public static ArrayList<Contact> getContactList(Context context) {
        ArrayList<Contact> list = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact contact = new Contact(id, name, Contact.Type.CONTACT, false);
                        list.add(contact);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return list;
    }

    public static ArrayList<Contact> sort(ArrayList<Contact> listContacts) {
        if (listContacts.size() > 0){
            Collections.sort(listContacts, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

            Contact contact = new Contact("", listContacts.get(0).getName().substring(0, 1), Contact.Type.TITLE, false);
            listContacts.add(0, contact);

            for (int i = 0; i < listContacts.size(); i++) {

                if (listContacts.indexOf(listContacts.get(i)) == listContacts.size() - 1) {
                    if (!listContacts.get(i).getName().substring(0, 1).equals(listContacts.get(i - 1).getName().substring(0, 1))) {
                        Contact contact1 = new Contact("", listContacts.get(i).getName().substring(0, 1), Contact.Type.TITLE, false);
                        listContacts.add(listContacts.indexOf(listContacts.get(i - 1)), contact1);
                    }
                    return listContacts;
                }

                if (!listContacts.get(i).getName().substring(0, 1).equals(listContacts.get(i + 1).getName().substring(0, 1))) {
                    Contact contact1 = new Contact("", listContacts.get(i + 1).getName().substring(0, 1), Contact.Type.TITLE, false);
                    listContacts.add(listContacts.indexOf(listContacts.get(i + 1)), contact1);
                }
            }
        }

        return listContacts;
    }

    public static boolean check(String text) {
        for (int i = 0; i < text.length() - 1; i++) {
            if (((int) text.charAt(i)) > 0 && ((int) text.charAt(i)) < 127) {
                Log.d(TAG, "check: " + text);
            } else {
                return true;
            }
        }
        return false;
    }
}
