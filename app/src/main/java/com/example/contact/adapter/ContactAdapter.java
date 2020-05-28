package com.example.contact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.R;
import com.example.contact.model.Contact;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ContactAdapter";

    private static int TYPE_CONTACT = 1;
    private static int TYPE_TITLE = 2;
    private ArrayList<Contact> listContacts = new ArrayList<>();
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setListContacts(ArrayList<Contact> listContacts) {
        this.listContacts.clear();
        this.listContacts.addAll(listContacts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_CONTACT) {
            return new ContactHolder(LayoutInflater.from(parent.getContext()).inflate(ContactHolder.LAYOUT_RES, parent, false));
        }
        return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(TitleHolder.LAYOUT_RES, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleHolder){
            TitleHolder titleHolder = (TitleHolder) holder;
            titleHolder.setTitle(listContacts.get(position).getName());
        }
        if (holder instanceof ContactHolder){
            ContactHolder contactHolder = (ContactHolder) holder;
            contactHolder.setName(listContacts.get(position).getName());

            contactHolder.itemView.setOnClickListener(v -> listener.itemContactClick(listContacts.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (listContacts.get(position).getType() == 0) {
            return TYPE_CONTACT;
        }
        return TYPE_TITLE;
    }

    class TitleHolder extends RecyclerView.ViewHolder {
        public static final int LAYOUT_RES = R.layout.item_view_title;
        @BindView(R.id.tv_title)
        TextView tvTitle;


        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setTitle(String s) {
            tvTitle.setText(s);
        }
    }

    class ContactHolder extends RecyclerView.ViewHolder {

        private static final int LAYOUT_RES = R.layout.item_view_contact;
        @BindView(R.id.im_avatar)
        CircleImageView imAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setName(String s) {
            tvName.setText(s);
        }
    }

    public interface Listener{
        void itemContactClick(Contact contact);
    }
}
