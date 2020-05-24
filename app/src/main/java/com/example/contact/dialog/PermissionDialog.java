package com.example.contact.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.contact.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PermissionDialog extends Dialog {

    @BindView(R.id.im_image)
    ImageView imImage;
    @BindView(R.id.tv_permission_text)
    TextView tvPermissionText;
    @BindView(R.id.btn_deny)
    Button btnDeny;
    @BindView(R.id.btn_allow)
    Button btnAllow;
    private DialogClick dialogClick;

    public void setDialogClick(DialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    public PermissionDialog(@NonNull Context context) {
        super(context, R.style.ThemeDialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        setContentView(R.layout.dialog_permission);
        ButterKnife.bind(this);
        listener();

    }

    private void listener() {
        btnAllow.setOnClickListener(v -> dialogClick.allow());

        btnDeny.setOnClickListener(v -> dialogClick.deny());
    }

    public void setTextButton(String s){
        btnAllow.setText(s);
    }

    public interface DialogClick {
        void allow();

        void deny();
    }
}
