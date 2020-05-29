package com.example.contact.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.contact.R;
import com.example.contact.dialog.PermissionDialog;
import com.example.contact.interfaces.Key;
import com.example.contact.utils.PermissionUtil;
import com.example.contact.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class EmojiActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = "EmopjiActivity";
    private EmojiconEditText editText;
    private EmojiconTextView textView;
    private Button button;
    private Button btnKeyEmoji;
    private Button btnKeyboard;
    private String id = "";
    private FrameLayout frameLayout;
    private RelativeLayout layOut;

    private String[] PERMISSIONS = {Manifest.permission.WRITE_CONTACTS};
    private boolean isDenyShowAgain = false;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private PermissionDialog permissionDialog;

    private Load load;
    private ProgressBar dialog;

    private ImageView imageView;
    private boolean checkKeyboard = false;

    @Override
    protected int LayoutRes() {
        return R.layout.activity_emoji;
    }

    @Override
    protected void initActivity() {
        textView = findViewById(R.id.txtEmojicon);
        editText = findViewById(R.id.editEmojicon);
        button = findViewById(R.id.btn_save);
        imageView = findViewById(R.id.im_emoji);
        btnKeyboard = findViewById(R.id.btn_keyboard);
        btnKeyEmoji = findViewById(R.id.btn_key_emoji);
        frameLayout = findViewById(R.id.frameMain);
        layOut = findViewById(R.id.rl_keyboard);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            load = new Load(EmojiActivity.this);
            load.execute();
        }, 100);

        dialog = findViewById(R.id.pb_load);

        permissionDialog = new PermissionDialog(this);

        Intent intent = getIntent();
        id = intent.getStringExtra(Key.KEY_ID);
        String name = intent.getStringExtra(Key.KEY_NAME);

        textView.setText(name);
        editText.setText(name);
        editText.setCursorVisible(false);
        editText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        focusRight();
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    protected void listener() {
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

                imageView.setImageBitmap(textAsBitmap(name, 95, Color.WHITE));
                imageView.invalidate();

                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                updateContact(id, bitmap);

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

        btnKeyboard.setOnClickListener(v -> {
            showKeyboard();

            Rect r = new Rect();
            View rootView = getWindow().getDecorView(); // this = activity
            rootView.getWindowVisibleDisplayFrame(r);
            int keyboardHeight = rootView.getHeight() - r.bottom;

            setMargin(keyboardHeight);

        });

        btnKeyEmoji.setOnClickListener(v -> {
            if (checkKeyboard) {
                hideKeyboard(btnKeyEmoji);
            }

            setMargin(0);
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
    protected void onPause() {
        super.onPause();
        if (checkKeyboard) {
            hideKeyboard(btnKeyEmoji);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout.setVisibility(View.VISIBLE);
        setMargin(0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        checkKeyboard = true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        checkKeyboard = false;
    }

    private void setMargin(int margin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, margin);
        layOut.setLayoutParams(layoutParams);
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

    private void updateContact(String id, Bitmap mBitmap) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        if (mBitmap != null) {
            // Picture
            try {
                ByteArrayOutputStream image = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, image);

                Uri rawContactUri = null;
                Cursor rawContactCursor = managedQuery(
                        ContactsContract.RawContacts.CONTENT_URI,
                        new String[]{ContactsContract.RawContacts._ID},
                        ContactsContract.RawContacts.CONTACT_ID + " = " + id,
                        null,
                        null);
                if (!rawContactCursor.isAfterLast()) {
                    rawContactCursor.moveToFirst();
                    rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendPath("" + rawContactCursor.getLong(0)).build();
                }
                rawContactCursor.close();

                ContentValues values = new ContentValues();
                int photoRow = -1;
                String where111 = ContactsContract.Data.RAW_CONTACT_ID + " == " +
                        ContentUris.parseId(rawContactUri) + " AND " + ContactsContract.Data.MIMETYPE + "=='" +
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
                Cursor cursor = managedQuery(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        where111,
                        null,
                        null);
                int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);
                if (cursor.moveToFirst()) {
                    photoRow = cursor.getInt(idIdx);
                }

                cursor.close();


                values.put(ContactsContract.Data.RAW_CONTACT_ID,
                        ContentUris.parseId(rawContactUri));
                values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
                values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray());
                values.put(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                if (photoRow >= 0) {
                    getContentResolver().update(
                            ContactsContract.Data.CONTENT_URI,
                            values,
                            ContactsContract.Data._ID + " = " + photoRow, null);
                } else {
                    getContentResolver().insert(
                            ContactsContract.Data.CONTENT_URI,
                            values);
                }
            } catch (Exception e) {
                Log.e("!_@@Image_Exception", e + "");
            }
        }
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e("@@@@@UPLOADERR", e + "");
        }
    }

    class Load extends AsyncTask<Void, Integer, Void> {

        private Activity activity;

        public Load(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            editText.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            EmojiconsFragment emojiconsFragment = new EmojiconsFragment();
            FragmentTransaction sr = getSupportFragmentManager().beginTransaction();
            sr.add(R.id.frameMain, emojiconsFragment);
            sr.commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            editText.setVisibility(View.VISIBLE);
            dialog.setVisibility(View.GONE);
        }
    }
}
