package com.service.calllog.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.service.calllog.core.CallLogPOSTModel;
import com.service.calllog.core.CallLogPrefs;
import com.service.calllog.core.CallLogService;
import com.service.calllog.R;
import com.service.calllog.ws.ApiClient;
import com.service.calllog.ws.ApiService;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    private CallLogService callLogService = new CallLogService();
    private boolean isServiceStarted;
    private Button serviceControl, updateWsUrl;
    private EditText urlInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceControl = (Button) findViewById(R.id.start_service);
        updateWsUrl = (Button) findViewById(R.id.update_ws_url);
        urlInput = (EditText) findViewById(R.id.input_post_url);

        isServiceStarted = checkIfServiceIsRunning(CallLogService.class);

        if (isServiceStarted) {
            serviceControl.setText("Stop service");
        } else {
            serviceControl.setText("Start service");
        }
        CallLogPrefs prefs = new CallLogPrefs(this);
        if (!TextUtils.isEmpty(prefs.getPostURL())) {
            urlInput.setHint(prefs.getPostURL());
        }

        serviceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isServiceStarted) {
                    updateService("Start service", false);
                    callLogService.stopService();
                } else {
                    updateService("Stop service", true);
                    startService(new Intent(MainActivity.this, CallLogService.class));
                }

            }
        });

        updateWsUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlInput.getText().toString();
                if (!isEmpty(url)) {
                    CallLogPrefs.setPostUrl(url);
                    urlInput.setText("");
                    urlInput.setHint(url);
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

    public long convertDateToMilis(Date date) {
        return date.getTime();
    }

    public void checkLog(String phNumber, String callType, String callDate, Date callDayTime,
                         String callDuration) {
        if (!CallLogPrefs.getLastSentLogID().equals(String.valueOf(convertDateToMilis(callDayTime)))) {
            CallLogPrefs.setSentLogID("" + callDayTime.getTime());

            Log.d("xtag", "time to post a new log with id: " + convertDateToMilis(callDayTime));

            CallLogPOSTModel callLogPOSTModel = new CallLogPOSTModel(phNumber, callType, callDate, callDuration);

            postPhoneLog(callLogPOSTModel);
        }
    }

    public void postPhoneLog(CallLogPOSTModel callLogPOSTModel) {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        Call<Void> call = apiService.sendPhoneLog(CallLogPrefs.getPostURL(), callLogPOSTModel);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

}

