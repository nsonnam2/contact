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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactFragment extends BaseFragment {

    private static final String TAG = "ContactFragment";
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    private Unbinder unbinder;

    private ArrayList<Contact> list = new ArrayList<>();
    private AlphabetAdapter alphabetAdapter;
    private RecyclerView rvAlphabet;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ArrayList<Contact> alphabet = new ArrayList<>();
    private  ContactAdapter contactAdapter;

    @Override
    protected int LayoutRes() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initFragment() {
        contactAdapter = new ContactAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvContact.setLayoutManager(layoutManager);
        rvContact.setAdapter(contactAdapter);

        alphabet.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == 0) {
                if (Utils.check(list.get(i).getName())) {
                    contacts.add(list.get(i));
                }
            }

            if (list.get(i).getType() == 1){
                alphabet.add(list.get(i));
            }
        }

        rvAlphabet = getActivity().findViewById(R.id.rv_alphabet);
        alphabetAdapter = new AlphabetAdapter(getContext());
        rvAlphabet.setAdapter(alphabetAdapter);
        alphabetAdapter.setList(alphabet);
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
            int index = Utils.getIndex(text, list);
            if (index > 0){
                rvContact.scrollToPosition(index);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        list.addAll(Utils.getContactList(getContext()));
        Utils.sort(list);

        contactAdapter.setListContacts(list);
        contactAdapter.notifyDataSetChanged();

        ReadFIle readFIle = new ReadFIle(new ReadFIle.CallBack() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(ArrayList<Contact> list) {
                list.clear();
                list.addAll(list);
            }
        });
        readFIle.execute(getContext());
    }
}
