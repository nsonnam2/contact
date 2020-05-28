package com.example.contact.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.contact.R;
import com.example.contact.dialog.PermissionDialog;
import com.example.contact.utils.PermissionUtil;
import com.example.contact.utils.Utils;

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
    private String[] PERMISSIONS = {Manifest.permission.WRITE_CONTACTS};
    private boolean isDenyShowAgain = false;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private PermissionDialog permissionDialog;
    private Load load;
    private Handler handler;
    private EmojiconsFragment emojiconsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);

        textView = findViewById(R.id.txtEmojicon);
        editText = findViewById(R.id.editEmojicon);
        button = findViewById(R.id.btn_save);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                load = new Load(EmojiActivity.this);
                load.execute();
            }
        }, 100);

        permissionDialog = new PermissionDialog(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");

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
            if (PermissionUtil.isGranted(EmojiActivity.this, PERMISSIONS[0])) {
                String name = textView.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(EmojiActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.updateContact(EmojiActivity.this, id, textView.getText().toString());
                finish();
            } else {
                permissionDialog.show();
                permissionDialog.setTextPermission("Allows permission to write your contacts");
                permissionDialog.setDialogClick(new PermissionDialog.DialogClick() {
                    @Override
                    public void allow() {
                        if (isDenyShowAgain) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        } else {
                            PermissionUtil.showPermission(EmojiActivity.this, PERMISSIONS[0], REQUEST_CODE_PERMISSION);
                        }
                        if (permissionDialog.isShowing()) {
                            permissionDialog.dismiss();
                        }
                    }

                    @Override
                    public void deny() {
                        permissionDialog.dismiss();
                    }
                });
            }
        });

    }


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        if (!editText.getText().toString().isEmpty()) {
            EmojiconsFragment.backspace(editText);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (PermissionUtil.isMarshmallow()) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            isDenyShowAgain = false;
                            permissionDialog.setTextButton("Allow");
                        } else {
                            isDenyShowAgain = true;
                            permissionDialog.setTextButton("Go to settings");
                        }
                    }
                } else {
                    Utils.updateContact(EmojiActivity.this, id, textView.getText().toString());
                    finish();
                }
            }
        }
    }

    private void focusRight() {
        int pos = editText.getText().length();
        editText.setSelection(pos);
    }

    class Load extends AsyncTask<Void, Integer, Void>{

        private Activity activity;

        public Load (Activity activity){
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            editText.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            emojiconsFragment = new  EmojiconsFragment();
            FragmentTransaction sr = getSupportFragmentManager().beginTransaction();
            sr.add(R.id.frameMain, emojiconsFragment);
            sr.commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            editText.setVisibility(View.VISIBLE);
        }
    }
}
