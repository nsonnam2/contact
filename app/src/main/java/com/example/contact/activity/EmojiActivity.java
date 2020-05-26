package com.example.contact.activity;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contact.R;
import com.example.contact.utils.Utils;

import java.util.ArrayList;

import emoji4j.EmojiUtils;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class EmojiActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = "EmopjiActivity";
    private EmojiconEditText editText;
    private EmojiconTextView textView;
    private Button button;
    private String id = "";
    private String name = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);

        getSupportFragmentManager().beginTransaction().commit();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");

        textView = findViewById(R.id.txtEmojicon);
        editText = findViewById(R.id.editEmojicon);
        button = findViewById(R.id.btn_save);

        textView.setText(name);
        editText.setText(name);
        editText.setCursorVisible(false);
        editText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        focusRight();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(s.toString());
                focusRight();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button.setOnClickListener(v -> {
            updateContact(id, editText.getText().toString());
            finish();
        });

    }

    private void updateContact( String id, String name){
        // TODO: 5/26/2020 change name of contacts

    }


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
       if (Utils.check(editText.getText().toString())){
           EmojiconsFragment.backspace(editText);
       }
    }

    private void focusRight(){
        int pos = editText.getText().length();
        editText.setSelection(pos);
    }

}
