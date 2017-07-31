package com.service.calllog;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private CallLogService callLogService = new CallLogService();
    private boolean isServiceStarted;
    private Button serviceControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceControl = (Button) findViewById(R.id.button);

        isServiceStarted = checkIfServiceIsRunning(CallLogService.class);

        if (isServiceStarted) {
            serviceControl.setText("Stop");
        } else {
            serviceControl.setText("Start");
        }

        serviceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isServiceStarted) {
                    updateService("Start", false);
                    callLogService.stopService();
                } else {
                    updateService("Stop", true);
                    startService(new Intent(MainActivity.this, CallLogService.class));
                }

            }
        });
    }

    private void updateService(String label, boolean serviceState) {
        serviceControl.setText(label);
        isServiceStarted = serviceState;
    }

    private boolean checkIfServiceIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
