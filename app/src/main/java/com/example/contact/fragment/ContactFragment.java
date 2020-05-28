package com.example.contact.fragment;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.R;
import com.example.contact.activity.EmojiActivity;
import com.example.contact.adapter.AlphabetAdapter;
import com.example.contact.adapter.ContactAdapter;
import com.example.contact.asyntask.ReadFIle;
import com.example.contact.interfaces.Key;
import com.example.contact.model.Contact;
import com.example.contact.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.Unbinder;

public class ContactFragment extends BaseFragment {

    private static final String TAG = "ContactFragment";

    @BindView(R.id.rv_contact)
    RecyclerView rvContact;

    private ArrayList<Contact> listContacts = new ArrayList<>();
    private ArrayList<Contact> alphabet = new ArrayList<>();

    private AlphabetAdapter alphabetAdapter;
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

        RecyclerView rvAlphabet = getActivity().findViewById(R.id.rv_alphabet);
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
            int index = Utils.getIndex(text, listContacts);
            if (index > 0){
                rvContact.scrollToPosition(index);
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
                listContacts.clear();
                listContacts.addAll(list);

                contactAdapter.setListContacts(Utils.sort(listContacts));

                ArrayList<Contact> contacts = new ArrayList<>();
                contacts.addAll(Utils.sort(list));
                alphabet.clear();
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getType() == 1){
                        alphabet.add(contacts.get(i));
                    }
                }
                alphabetAdapter.setList(alphabet);
            }
        });

        readFIle.execute(getContext());
    }
}
