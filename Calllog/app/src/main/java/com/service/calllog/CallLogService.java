package com.service.calllog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ghitaistrate on 31/07/2017.
 */

public class CallLogService extends Service {

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
        telephonyManager.listen(new CallLogStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

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
        if (runnable != null){
            handler.removeCallbacks(runnable);
            Log.d("xtag", "Service killed");
        } else {
            Log.d("xtag", "Service runnable == null");
        }

    }

}
