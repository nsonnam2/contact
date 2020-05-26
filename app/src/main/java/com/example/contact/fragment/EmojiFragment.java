package com.example.contact.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.contact.model.Contact;
import com.example.contact.utils.Utils;

import java.util.ArrayList;

public class EmojiFragment extends Fragment {
    private static final String TAG = "EmojiFragment";

    private RecyclerView rvContacts;
    private RecyclerView rvAlphabet;
    private ArrayList<Contact> listContacts = new ArrayList<>();
    private ArrayList<Contact> listEmoji = new ArrayList<>();
    private ArrayList<Contact> listAlphabet = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emoji, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rvContacts = getActivity().findViewById(R.id.rv_contact_emoji);
        rvAlphabet = getActivity().findViewById(R.id.rv_alphabet_emoji);

        ContactAdapter contactAdapter = new ContactAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setAdapter(contactAdapter);

        listContacts.clear();
        listContacts.addAll(Utils.getContactList(getContext()));


        listEmoji.clear();
        for (int i = 0; i < listContacts.size(); i++) {
            if (Utils.check(listContacts.get(i).getName())){
                listEmoji.add(listContacts.get(i));
            }
        }

        contactAdapter.setListContacts(Utils.sort(listEmoji));

        contactAdapter.setListener(contact -> {
            Intent intent = new Intent(getContext(), EmojiActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("id", contact.getId());
            startActivity(intent);
        });


        AlphabetAdapter alphabetAdapter = new AlphabetAdapter(getContext());
        rvAlphabet.setAdapter(alphabetAdapter);

        listAlphabet.clear();
        for (int i = 0; i < listEmoji.size(); i++) {
            if (listEmoji.get(i).getType() == Contact.Type.TITLE){
                listAlphabet.add(listEmoji.get(i));
            }
        }

        alphabetAdapter.setList(listAlphabet);

        alphabetAdapter.setListener(new AlphabetAdapter.Listener() {
            @Override
            public void itemClick(Contact contact) {
                String text = contact.getName();
                int index = getIndex(text, listEmoji);
                if (index > 0){
                    rvContacts.scrollToPosition(index);
                }
            }
        });


    }

    private int getIndex(String text, ArrayList<Contact> list){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().toLowerCase().startsWith(text.toLowerCase()) && list.get(i).getType() == Contact.Type.CONTACT){
                return list.indexOf(list.get(i));
            }
        }

        return -1;
    }

}
