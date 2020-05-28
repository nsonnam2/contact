package com.example.contact.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutRes());

        ButterKnife.bind(this);

        initActivity();
        listener();
    }

    protected abstract int LayoutRes();

    protected abstract void initActivity();

    protected abstract void listener();
}
