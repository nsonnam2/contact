package com.example.contact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.R;
import com.example.contact.model.Contact;

import java.util.ArrayList;

public class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.AlphabetHoilder> {
    private LayoutInflater layoutInflater;
    private ArrayList<Contact> list = new ArrayList<>();
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public AlphabetAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setList(ArrayList<Contact> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlphabetHoilder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_view_alphabet, parent, false);
        return new AlphabetHoilder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlphabetHoilder holder, int position) {
        holder.bindData(list.get(position));
        holder.itemView.setOnClickListener(v -> listener.itemClick(list.get(position)));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class AlphabetHoilder extends RecyclerView.ViewHolder {
        private TextView textView;
        public AlphabetHoilder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_text);
        }

        private void bindData(Contact contact){
            textView.setText(contact.getName());
        }
    }

    public interface Listener{
        void itemClick(Contact contact);
    }
}
