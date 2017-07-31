package com.service.calllog.core;

/**
 * Created by ghitaistrate on 31/07/2017.
 */

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallLogStateListener extends PhoneStateListener {

//    private static final String TAG = "CallLogStateListener";

    public CallLogStateListener() {
        super();
        Log.d("xtag", "listener_init");
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        Log.d("xtag", "listener_state_change");
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                //when Idle i.e no call
                Log.d("xtag", "listener_idle");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //when Off hook i.e in call
                //Make intent and start your service here
                Log.d("xtag", "listener_offhook");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                //when Ringing
                Log.d("xtag", "listener_ringing");
                break;
            default:
                break;
        }
    }
}
