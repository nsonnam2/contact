package com.example.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contact.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emoji4j.EmojiUtils;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;

public class EmopjiActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = "EmopjiActivity";
    private EmojiconEditText editText;
    private EmojiconTextView textView;
    private String id = "";
    private String name = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");

        textView = findViewById(R.id.txtEmojicon);

        textView.setText(name);


        getSupportFragmentManager().beginTransaction().commit();

    }

    @Override
    public void onEmojiconClicked(io.github.rockerhieu.emojicon.emoji.Emojicon emojicon) {
        String s = name += emojicon.getEmoji();
        textView.setText(s);

//        Log.d(TAG, "onEmojiconClicked: " + emojicon.getEmoji());
//        textView.setText(name + emojicon.getEmoji());
        Log.d(TAG, "onEmojiconClicked: " + EmojiUtils.shortCodify(s));
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
//        Collection<Emoji> collection = new ArrayList<Emoji>();
//        collection.add(EmojiManager.getForAlias("wink"));
//        Log.d(TAG, "onEmojiconBackspaceClicked: " + EmojiParser.removeEmojis(textView.getText().toString(), collection));

        Log.d(TAG, "onEmojiconBackspaceClicked: " + EmojiUtils.shortCodify(getLastEmoji(textView.getText().toString())));
        String s = textView.getText().toString();
        if (s.endsWith(":")){
            // TODO: 5/25/2020 delete single last emoji 
        }

    }

    public String getLastEmoji(String name) {
        String regexPattern = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";
        byte[] utf8 = new byte[0];
        String string1 = null;

        try {
            utf8 = name.getBytes("UTF-8");
            string1 = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(string1);
        ArrayList<String> matchList = new ArrayList<String>();

        while (matcher.find()) {
            matchList.add(matcher.group());
        }

        // TODO: 5/25/2020 get single last emoji 
        return matchList.get(matchList.size() - 1);

    }
}
