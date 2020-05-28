package com.example.contact.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    public static boolean checkPermission(Context context, String permission) {
        if (isMarshmallow()) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isGranted(Context context, String permission) {
        return PermissionUtil.checkPermission(context, permission);
    }

    public static void showPermission(Activity activity, String permission, int request) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{permission},
                request);
    }
}
