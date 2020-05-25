package com.example.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.contact.R;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emoji4j.EmojiUtils;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

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

    private void setEmojiconFragment(boolean useSystemdefault) {
        getSupportFragmentManager().beginTransaction().commit();
    }

    String emoji = "";

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
//        Log.d(TAG, "onEmojiconClicked: " + emojicon.getEmoji());
//        textView.setText(name + emojicon.getEmoji());
        Log.d(TAG, "onEmojiconClicked: " + EmojiUtils.shortCodify(emojicon.getEmoji()));
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
//        Collection<Emoji> collection = new ArrayList<Emoji>();
//        collection.add(EmojiManager.getForAlias("wink"));
//        Log.d(TAG, "onEmojiconBackspaceClicked: " + EmojiParser.removeEmojis(textView.getText().toString(), collection));

        main(textView.getText().toString()
        );

    }



    public static String mysqlSafe(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (i < (input.length() - 1)) { // Emojis are two characters long in java, e.g. a rocket emoji is "\uD83D\uDE80";
                if (Character.isSurrogatePair(input.charAt(i), input.charAt(i + 1))) {
                    i += 1; //also skip the second character of the emoji
                    continue;
                }
            }
            sb.append(input.charAt(i));
        }

        return sb.toString();
    }

        public void main(String name) {
            String regexPattern = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";
            byte[] utf8 = new byte[0];
            try {
                utf8 = name.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String string1 = null;
            try {
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

            for(int i=0;i<matchList.size();i++){
                Log.d(TAG, "mainnn: " + EmojiUtils.shortCodify(matchList.get(i)));
            }
        }

}
