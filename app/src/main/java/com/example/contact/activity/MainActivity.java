package com.example.contact.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.contact.R;
import com.example.contact.adapter.ViewPagerAdapter;
import com.example.contact.dialog.PermissionDialog;
import com.example.contact.fragment.ContactFragment;
import com.example.contact.fragment.EmojiFragment;
import com.example.contact.utils.PermissionUtil;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.im_search)
    LinearLayout imSearch;
    @BindView(R.id.im_menu)
    LinearLayout imMenu;
    @BindView(R.id.rl_actionBar)
    RelativeLayout rlActionBar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.ln_search)
    RelativeLayout lnSearch;
    @BindView(R.id.tv_app_name)
    TextView tvAppName;
    @BindView(R.id.im_back)
    ImageView imBack;
    @BindView(R.id.im_close)
    ImageView imClose;

    private String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private boolean isDenyShowAgain = false;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private PermissionDialog permissionDialog;

    private ViewPagerAdapter viewPagerAdapter;
    private ContactFragment contactFragment = new ContactFragment();
    private EmojiFragment emojiFragment = new EmojiFragment();
    private Search search;

    public void setSearch(Search search) {
        this.search = search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        permissionDialog = new PermissionDialog(this);

        loadViewSearch(false);

        listener();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void listener() {
        imMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, imMenu);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.rate:
                            Toast.makeText(MainActivity.this, "Rate", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.about:
                            Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.privacy:
                            Toast.makeText(MainActivity.this, "Privacy policy", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        });

        imSearch.setOnClickListener(v -> {
            loadViewSearch(true);
        });

        imBack.setOnClickListener(v -> {
            loadViewSearch(false);
            clearText(edtSearch);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        });

        imClose.setOnClickListener(v -> clearText(edtSearch));

    }

    private void clearText(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return;
        }
        editText.setText("");

    }

    @Override
    public void onBackPressed() {
        if (lnSearch.getVisibility() == View.VISIBLE) {
            loadViewSearch(false);
            clearText(edtSearch);
        }else {
            finish();
        }
    }

    private void loadViewSearch(boolean b) {
        if (b) {
            tvAppName.setVisibility(View.GONE);
            imSearch.setVisibility(View.GONE);
            imMenu.setVisibility(View.GONE);
            lnSearch.setVisibility(View.VISIBLE);
        } else {
            tvAppName.setVisibility(View.VISIBLE);
            imSearch.setVisibility(View.VISIBLE);
            imMenu.setVisibility(View.VISIBLE);
            lnSearch.setVisibility(View.GONE);
        }
    }

    private void initActivity() {

        Fragment[] fms = {contactFragment, emojiFragment};

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fms);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (String s : PERMISSIONS) {
            if (isGranted(s)) {
                if (permissionDialog.isShowing()) {
                    permissionDialog.dismiss();
                }
                initActivity();
            } else {
                permissionDialog.show();
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
                    }

                    @Override
                    public void deny() {
                        finish();
                    }
                });
            }

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
                    initActivity();
                }
            }
        }
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

    public interface Search {
        void search(String s);
    }
}
