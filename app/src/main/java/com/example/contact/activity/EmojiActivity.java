package com.example.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contact.R;
import com.example.contact.utils.Utils;

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
        id = intent.getStringExtra("id"); // id của danh bạ
        name = intent.getStringExtra("name");

        textView = findViewById(R.id.txtEmojicon);
        editText = findViewById(R.id.editEmojicon);
        button = findViewById(R.id.btn_save);

        editText.setText(name);
        editText.requestFocus();
        editText.setCursorVisible(false);


        button.setOnClickListener(v -> {
            // nút đổi tên
            update(editText.getText().toString(), id);
            Log.d(TAG, "onCreate: " + id);
            finish();
        });
    }

    private void update(String newName, String id) {
        // TODO: 5/26/2020 viết code đổi tên ở đây

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText, emojicon);

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        String text = editText.getText().toString();
       if (Utils.check(text)){
           EmojiconsFragment.backspace(editText);
       }else {
           Log.d(TAG, "onEmojiconBackspaceClicked: ");
       }
    }

}
