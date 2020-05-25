package com.example.contact.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.R;
import com.example.contact.activity.EmopjiActivity;
import com.example.contact.activity.MainActivity;
import com.example.contact.adapter.ContactAdapter;
import com.example.contact.dialog.EmojiDialog;
import com.example.contact.model.Contact;
import com.example.contact.utils.Utils;
import com.vdurmont.emoji.EmojiManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactFragment extends Fragment {
    private static final String TAG = "ContactFragment";
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    private Unbinder unbinder;

    private ArrayList<Contact> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ContactAdapter contactAdapter = new ContactAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvContact.setLayoutManager(layoutManager);
        rvContact.setAdapter(contactAdapter);

        list.clear();
        list.addAll(getContactList(getContext()));
        Utils.sort(list);

        contactAdapter.setListContacts(list);

        contactAdapter.setListener(new ContactAdapter.Listener() {
            @Override
            public void itemContactClick(Contact contact) {
//               EmojiDialog emojiDialog = new EmojiDialog();
//               emojiDialog.show(getFragmentManager(), null);

                Intent intent = new Intent(getContext(), EmopjiActivity.class);
                intent.putExtra("name", contact.getName());
                intent.putExtra("id", contact.getId());
                startActivity(intent);
            }
        });

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSearch(s -> {
            ArrayList<Contact> listContacts = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toLowerCase().contains(s.toLowerCase()) && list.get(i).getType() == Contact.Type.CONTACT) {
                    listContacts.add(list.get(i));
                }
            }

            contactAdapter.setListContacts(Utils.sort(listContacts));
            contactAdapter.notifyDataSetChanged();
        });

        ArrayList<Contact> contacts = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == Contact.Type.CONTACT){
                if (check(list.get(i))){
                    contacts.add(list.get(i));
                }
            }
        }

        for (int i = 0; i < contacts.size(); i++) {
            Log.d(TAG, "onActivityCreated: " + contacts.get(i).getName());
        }

    }

    private boolean check(Contact contact) {
        String userinp = contact.getName();
        for (int i = 0; i < userinp.length() - 1; i++) {
            if (((int) userinp.charAt(i)) > 0 && ((int) userinp.charAt(i)) < 127) {
                Log.d(TAG, "check: " + userinp);
            } else {
                return true;
            }
        }
        return false;
    }


    public static void updateContactName(Context context, String contactId, String newName) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.CommonDataKinds.Phone._ID + "=? AND " +
                                    ContactsContract.Data.MIMETYPE + "='" +
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                            new String[]{contactId})
                    .withValue(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, newName)
                    .build());

            ContentProviderResult[] result = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {

        }
    }


    private ArrayList<Contact> getContactList(Context context) {
        ArrayList<Contact> list = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
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

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
