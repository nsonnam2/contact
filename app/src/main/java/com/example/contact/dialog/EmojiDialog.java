package com.example.contact.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.contact.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class EmojiDialog extends Dialog {
    @BindView(R.id.tv_emoji)
    EmojiconTextView tvEmoji;
    @BindView(R.id.edt_emoji)
    EmojiconEditText edtEmoji;
    @BindView(R.id.btn_save)
    Button btnSave;

    public EmojiDialog(@NonNull Context context) {
        super(context, R.style.ThemeDialog);
        setContentView(R.layout.dialog_emoji);
        ButterKnife.bind(this);


    }
}
