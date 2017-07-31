package com.service.calllog;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        getCallDetails();
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

    public long convertDateToMilis(Date date) {
        return date.getTime();
    }

    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
//        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String idul = managedCursor.getString(id);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir +
                    " \nCall Date:--- " + convertDateToMilis(callDayTime) + " \nCall duration in sec :--- " + callDuration +
                    " \nCall ID:--- " + idul);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        ((TextView) findViewById(R.id.call)).setText(sb);
    }
}
