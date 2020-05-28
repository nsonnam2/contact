package com.example.contact.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.contact.R;
import com.example.contact.adapter.ContactAdapter;
import com.example.contact.adapter.ViewPagerAdapter;
import com.example.contact.asyntask.ReadFIle;
import com.example.contact.dialog.PermissionDialog;
import com.example.contact.fragment.ContactFragment;
import com.example.contact.fragment.EmojiFragment;
import com.example.contact.interfaces.Key;
import com.example.contact.model.Contact;
import com.example.contact.utils.PermissionUtil;
import com.example.contact.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

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
    private RelativeLayout rlSearch;
    private RecyclerView rvSearch;

    private String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private boolean isDenyShowAgain = false;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private PermissionDialog permissionDialog;

    private ViewPagerAdapter viewPagerAdapter;
    private ContactAdapter contactAdapter;

    private ContactFragment contactFragment = new ContactFragment();
    private EmojiFragment emojiFragment = new EmojiFragment();
    private ArrayList<Contact> listContacts = new ArrayList<>();
    private ArrayList<Contact> listSearch = new ArrayList<>();
    private ArrayList<Contact> listEmoji = new ArrayList<>();

    int tabPosition = -1;
    private String textSearch = "";
    private boolean check = false;

    @Override
    protected int LayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initActivity() {
        rlSearch = findViewById(R.id.rl_search);
        rvSearch = findViewById(R.id.rv_search);

        permissionDialog = new PermissionDialog(this);

        loadViewSearch(false);

        contactAdapter = new ContactAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rvSearch.setLayoutManager(layoutManager);
        rvSearch.setAdapter(contactAdapter);
    }

    @Override
    protected void listener() {
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

            int frag = tabLayout.getSelectedTabPosition();

            listSearch.clear();

            if (frag == 0) {
                listSearch.addAll(listContacts);
                contactAdapter.setListContacts(Utils.sort(listSearch));
            }

            if (frag == 1) {
                listSearch.addAll(listEmoji);
                contactAdapter.setListContacts(Utils.sort(listSearch));
            }

            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    textSearch = s.toString();

                    ArrayList<Contact> rs = new ArrayList<>();

                    if (frag == 0) {
                        for (int i = 0; i < listContacts.size(); i++) {
                            if (listContacts.get(i).getName().toLowerCase().contains(s.toString().toLowerCase()) && listContacts.get(i).getType() == 0) {
                                rs.add(listContacts.get(i));
                            }
                        }
                    }

                    if (frag == 1) {
                        for (int i = 0; i < listEmoji.size(); i++) {
                            if (listEmoji.get(i).getName().toLowerCase().contains(s.toString().toLowerCase()) && listEmoji.get(i).getType() == 0) {
                                rs.add(listEmoji.get(i));
                            }
                        }
                    }

                    contactAdapter.setListContacts(Utils.sort(rs));
                    contactAdapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

        });

        imBack.setOnClickListener(v -> {
            loadViewSearch(false);
            clearText(edtSearch);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        });

        imClose.setOnClickListener(v -> clearText(edtSearch));

        contactAdapter.setListener(contact -> {
            Intent intent = new Intent(MainActivity.this, EmojiActivity.class);
            intent.putExtra(Key.KEY_NAME, contact.getName());
            intent.putExtra(Key.KEY_ID, contact.getId());
            startActivity(intent);
        });

    }

    private void readContact(){
        ReadFIle readFIle = new ReadFIle(new ReadFIle.CallBack() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(ArrayList<Contact> list) {
                listContacts.clear();
                listEmoji.clear();

                listContacts.addAll(list);

                for (int i = 0; i < listContacts.size(); i++) {
                    if (Utils.check(listContacts.get(i).getName())) {
                        listEmoji.add(listContacts.get(i));
                    }
                }

                if (!check){
                    Fragment[] fms = {contactFragment, emojiFragment};
                    viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fms);
                    viewPager.setAdapter(viewPagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                }

                check = true;

            }
        });

        readFIle.execute(MainActivity.this);
    }

    private void clearText(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return;
        }
        editText.setText("");

    }

    private void loadViewSearch(boolean b) {
        if (b) {
            tvAppName.setVisibility(View.GONE);
            imSearch.setVisibility(View.GONE);
            imMenu.setVisibility(View.GONE);
            lnSearch.setVisibility(View.VISIBLE);
            rlSearch.setVisibility(View.VISIBLE);
        } else {
            tvAppName.setVisibility(View.VISIBLE);
            imSearch.setVisibility(View.VISIBLE);
            imMenu.setVisibility(View.VISIBLE);
            lnSearch.setVisibility(View.GONE);
            rlSearch.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtil.isGranted(MainActivity.this,PERMISSIONS[0])) {

            readContact();

            ArrayList<Contact> list = new ArrayList<>();

            if (rvSearch.getVisibility() == View.VISIBLE){
                if (textSearch.isEmpty()){
                    if (tabPosition == 0){
                        list.addAll(listContacts);
                    }
                    if (tabPosition == 1){
                        list.addAll(listEmoji);
                    }
                }else {
                    if (tabPosition == 0) {
                        for (int i = 0; i < listContacts.size(); i++) {
                            if (listContacts.get(i).getName().toLowerCase().contains(textSearch.toLowerCase()) && listContacts.get(i).getType() == 0) {
                                list.add(listContacts.get(i));
                            }
                        }
                    }
                    if (tabPosition == 1) {
                        for (int i = 0; i < listEmoji.size(); i++) {
                            if (listEmoji.get(i).getName().toLowerCase().contains(textSearch.toLowerCase()) && listEmoji.get(i).getType() == 0) {
                                list.add(listEmoji.get(i));
                            }
                        }
                    }
                }

                contactAdapter.setListContacts(Utils.sort(list));
                contactAdapter.notifyDataSetChanged();
            }
        } else {
            permissionDialog.show();
            permissionDialog.setTextPermission("Allows permission to read your contacts");
            permissionDialog.setDialogClick(new PermissionDialog.DialogClick() {
                @Override
                public void allow() {
                    if (isDenyShowAgain) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        PermissionUtil.showPermission(MainActivity.this,PERMISSIONS[0], REQUEST_CODE_PERMISSION);
                    }
                    if (permissionDialog.isShowing()) {
                        permissionDialog.dismiss();
                    }
                }

                @Override
                public void deny() {
                    finish();
                }
            });

        }

        if (tabPosition > 0){
          tabLayout.getTabAt(tabPosition).select();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        tabPosition = tabLayout.getSelectedTabPosition();
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
                    readContact();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (lnSearch.getVisibility() == View.VISIBLE) {
            loadViewSearch(false);
            clearText(edtSearch);
        } else {
            finish();
        }
    }
}
