package com.example.contact.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.contact.R;
import com.example.contact.dialog.PermissionDialog;
import com.example.contact.utils.PermissionUtil;
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
    private String[] PERMISSIONS = {Manifest.permission.WRITE_CONTACTS};
    private boolean isDenyShowAgain = false;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private PermissionDialog permissionDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);


        getSupportFragmentManager().beginTransaction().commit();
        permissionDialog = new PermissionDialog(this);

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
            if (isGranted(PERMISSIONS[0])) {
                String name = textView.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(EmojiActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateContact(id, textView.getText().toString());
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
                            showPermission(PERMISSIONS[0]);
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

    private void focusRight() {
        int pos = editText.getText().length();
        editText.setSelection(pos);
    }

    private boolean isGranted(String permission) {
        return PermissionUtil.checkPermission(this, permission);
    }

    private void showPermission(String permission) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{permission},
                REQUEST_CODE_PERMISSION);
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
                    updateContact(id, textView.getText().toString());
                }
            }
        }
    }

    private void updateContact(String id, String name) {
        try {
            ContentResolver contentResolver = EmojiActivity.this.getContentResolver();

            String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

            String[] nameParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

            ArrayList<android.content.ContentProviderOperation> ops = new ArrayList<>();

            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
