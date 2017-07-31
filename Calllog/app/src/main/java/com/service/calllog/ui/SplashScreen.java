package com.service.calllog.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.service.calllog.core.CallLogPrefs;
import com.service.calllog.ui.MainActivity;

/**
 * Created by Ghita on 31/07/2017.
 */

public class SplashScreen extends Activity {

    String[] permissions = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE};


    @Override
    protected void onResume() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                startCallLog();
            }
        } else {
            startCallLog();
        }
        super.onResume();
    }

    public void startCallLog() {
        startActivity(new Intent(this, MainActivity.class));
    }


    private boolean checkIfAlreadyhavePermission() {
        int callLog = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        int phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (callLog == PackageManager.PERMISSION_GRANTED &&
                phoneState == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this,
                permissions, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    startCallLog();
                } else {
                    //not granted
                    Toast.makeText(this, "You need to grant permission to continue", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
