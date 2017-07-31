package com.service.calllog;

/**
 * Created by ghitaistrate on 31/07/2017.
 */

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallLogStateListener extends PhoneStateListener {

    //private static final String TAG = "PhoneStateChanged";
    Context context; //Context to make Toast if required

    public CallLogStateListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

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
