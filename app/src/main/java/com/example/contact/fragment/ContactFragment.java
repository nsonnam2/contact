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
import com.example.contact.adapter.AlphabetAdapter;
import com.example.contact.activity.EmojiActivity;
import com.example.contact.activity.MainActivity;
import com.example.contact.adapter.ContactAdapter;
import com.example.contact.model.Contact;
import com.example.contact.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactFragment extends Fragment {
    private static final String TAG = "ContactFragment";
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    private Unbinder unbinder;

    private ArrayList<Contact> list = new ArrayList<>();
    private AlphabetAdapter alphabetAdapter;
    private RecyclerView rvAlphabet;
    private ArrayList<Contact> contacts = new ArrayList<>(); // list contains emoji
    private ArrayList<Contact> alphabet = new ArrayList<>();

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
        list.addAll(Utils.getContactList(getContext()));
        Utils.sort(list);

        contactAdapter.setListContacts(list);

        contactAdapter.setListener(contact -> {
            Intent intent = new Intent(getContext(), EmojiActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("id", contact.getId());
            startActivity(intent);
        });

        alphabet.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == Contact.Type.CONTACT) {
                if (Utils.check(list.get(i).getName())) {
                    contacts.add(list.get(i));
                }
            }

            if (list.get(i).getType() == Contact.Type.TITLE){
                alphabet.add(list.get(i));
            }
        }

        rvAlphabet = getActivity().findViewById(R.id.rv_alphabet);
        alphabetAdapter = new AlphabetAdapter(getContext());
        rvAlphabet.setAdapter(alphabetAdapter);
        alphabetAdapter.setList(alphabet);

        alphabetAdapter.setListener(contact -> {
            String text = contact.getName();
            int index = getIndex(text, list);
            if (index > 0){
                rvContact.scrollToPosition(index);
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

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
