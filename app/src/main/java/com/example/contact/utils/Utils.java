package com.example.contact.utils;

import android.util.Log;

import com.example.contact.model.Contact;

import java.util.ArrayList;
import java.util.Collections;

public class Utils {

    private static final String TAG = "Utils";

    public static ArrayList<Contact> sort(ArrayList<Contact> listContacts) {
        if (listContacts.size() > 0){
            Collections.sort(listContacts, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

            Contact contact = new Contact("", listContacts.get(0).getName().substring(0, 1), Contact.Type.TITLE);
            listContacts.add(0, contact);

            for (int i = 0; i < listContacts.size(); i++) {

                if (listContacts.indexOf(listContacts.get(i)) == listContacts.size() - 1) {
                    if (!listContacts.get(i).getName().substring(0, 1).equals(listContacts.get(i - 1).getName().substring(0, 1))) {
                        Contact contact1 = new Contact("", listContacts.get(i).getName().substring(0, 1), Contact.Type.TITLE);
                        listContacts.add(listContacts.indexOf(listContacts.get(i - 1)), contact1);
                    }
                    return listContacts;
                }

                if (!listContacts.get(i).getName().substring(0, 1).equals(listContacts.get(i + 1).getName().substring(0, 1))) {
                    Contact contact1 = new Contact("", listContacts.get(i + 1).getName().substring(0, 1), Contact.Type.TITLE);
                    listContacts.add(listContacts.indexOf(listContacts.get(i + 1)), contact1);
                }
            }
        }

        return listContacts;
    }
}
