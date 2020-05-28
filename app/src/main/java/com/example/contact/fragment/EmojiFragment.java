package com.example.contact.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.R;
import com.example.contact.activity.EmojiActivity;
import com.example.contact.activity.MainActivity;
import com.example.contact.adapter.AlphabetAdapter;
import com.example.contact.adapter.ContactAdapter;
import com.example.contact.asyntask.ReadFIle;
import com.example.contact.interfaces.Key;
import com.example.contact.model.Contact;
import com.example.contact.utils.Utils;

import java.util.ArrayList;

public class EmojiFragment extends BaseFragment {
    private static final String TAG = "EmojiFragment";

    private RecyclerView rvContacts;
    private RecyclerView rvAlphabet;
    private ArrayList<Contact> listContacts = new ArrayList<>();
    private ArrayList<Contact> listEmoji = new ArrayList<>();
    private ArrayList<Contact> listAlphabet = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private AlphabetAdapter alphabetAdapter;

    @Override
    protected int LayoutRes() {
        return R.layout.fragment_emoji;
    }

    @Override
    protected void initFragment() {
        rvContacts = getActivity().findViewById(R.id.rv_contact_emoji);
        rvAlphabet = getActivity().findViewById(R.id.rv_alphabet_emoji);

        contactAdapter = new ContactAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setAdapter(contactAdapter);

        alphabetAdapter = new AlphabetAdapter(getContext());
        rvAlphabet.setAdapter(alphabetAdapter);

    }

    @Override
    protected void listener() {
        contactAdapter.setListener(contact -> {
            Intent intent = new Intent(getContext(), EmojiActivity.class);
            intent.putExtra(Key.KEY_NAME, contact.getName());
            intent.putExtra(Key.KEY_ID, contact.getId());
            startActivity(intent);
        });

        alphabetAdapter.setListener(contact -> {
            String text = contact.getName();
            int index = Utils.getIndex(text, listEmoji);
            if (index > 0) {
                rvContacts.scrollToPosition(index);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ReadFIle readFIle = new ReadFIle(new ReadFIle.CallBack() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(ArrayList<Contact> list) {
                listEmoji.clear();
                for (int i = 0; i < list.size(); i++) {
                    if (Utils.check(list.get(i).getName())) {
                        listEmoji.add(list.get(i));
                    }
                }

                Log.d("Namns", "onPostExecute: 1 - " + listEmoji.size());
                contactAdapter.setListContacts(Utils.sort(listEmoji));
                Log.d("Namns", "onPostExecute: 2 - " + listEmoji.size());

                // TODO: 5/28/2020 fix logic later
                listEmoji.clear();
                for (int i = 0; i < list.size(); i++) {
                    if (Utils.check(list.get(i).getName())) {
                        listEmoji.add(list.get(i));
                    }
                }

                ArrayList<Contact> contacts = new ArrayList<>();
                contacts.addAll(Utils.sort(listEmoji));

                listAlphabet.clear();
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getType() == 1) {
                        listAlphabet.add(contacts.get(i));
                    }
                }

                alphabetAdapter.setList(listAlphabet);
            }
        });

        readFIle.execute(getContext());
    }
}
