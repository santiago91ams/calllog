package com.service.calllog.core;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.service.calllog.ui.MainActivity;

import java.util.Date;

/**
 * Created by ghitaistrate on 31/07/2017.
 */

public class CallLogService extends Service implements QueryCallLog {

    public Context context = this;
    public static Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("xtag", "Service created");

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new CallLogStateListener(this), PhoneStateListener.LISTEN_CALL_STATE);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d("xtag", "Service still running");
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }

    @Override
    public void onDestroy() {
        Log.d("xtag", "Service killed");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Log.d("xtag", "Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void stopService() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            Log.d("xtag", "Service killed");
        } else {
            Log.d("xtag", "Service runnable == null");
        }

    }

    @Override
    public void getCallDetails() {
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        if (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);

            switch (Integer.parseInt(callType)) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    callType = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    callType = "MISSED";
                    break;
                case CallLog.Calls.VOICEMAIL_TYPE:
                    callType = "VOICEMAIL";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    callType = "REJECTED";
                    break;
                case CallLog.Calls.BLOCKED_TYPE:
                    callType = "BLOCKED";
                    break;

            }

            CallLogPrefs prefs = new CallLogPrefs(this);
            MainActivity mainActivity = new MainActivity();
            mainActivity.checkLog(phNumber, callType, callDate, callDayTime, callDuration);
        }

        managedCursor.close();
    }
}
