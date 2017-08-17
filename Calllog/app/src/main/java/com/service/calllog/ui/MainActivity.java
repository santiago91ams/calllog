package com.service.calllog.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.calllog.core.CallLogPOSTModel;
import com.service.calllog.core.CallLogPrefs;
import com.service.calllog.core.CallLogService;
import com.service.calllog.R;
import com.service.calllog.core.MyService;
import com.service.calllog.database.HelperDB;
import com.service.calllog.ws.ApiClient;
import com.service.calllog.ws.ApiService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    private CallLogService callLogService = new CallLogService();
    private Button serviceControl, updateWsUrl;
    private EditText urlInput;
    public static String dateFormat = "dd-MM-yyyy HH:mm:ss";
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceControl = (Button) findViewById(R.id.start_service);
        updateWsUrl = (Button) findViewById(R.id.update_ws_url);
        urlInput = (EditText) findViewById(R.id.input_post_url);
        urlInput.setImeActionLabel("Ready to log", EditorInfo.IME_ACTION_DONE);
        CallLogPrefs prefs = new CallLogPrefs(this);

        if (checkIfServiceIsRunning(CallLogService.class)) {
            updateService("Stop service");
            CallLogPrefs.setServiceRunning(true);
        } else {
            updateService("Start service");
            CallLogPrefs.setServiceRunning(false);
        }

        if (!TextUtils.isEmpty(prefs.getPostURL())) {
            urlInput.setHint(prefs.getPostURL());
        }

        serviceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty(CallLogPrefs.getPostURL())) {
                    if (checkIfServiceIsRunning(CallLogService.class)) {
                        updateService("Start service");
                        stopService(new Intent(MainActivity.this, CallLogService.class));
                        CallLogPrefs.setServiceRunning(false);
                    } else {
                        updateService("Stop service");
                        startService(new Intent(MainActivity.this, CallLogService.class));
                        CallLogPrefs.setServiceRunning(true);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Update the post url first!", Toast.LENGTH_SHORT).show();
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

    private void updateService(String label) {
        serviceControl.setText(label);
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

            Log.d("xtag", "time to post a new log with id: " + convertDateToMilis(callDayTime));

            CallLogPOSTModel callLogPOSTModel = new CallLogPOSTModel(
                    phNumber,
                    callType,
//                    String.valueOf(android.text.format.DateFormat.format(dateFormat, callDayTime)),
                    callDate,
                    callDuration);

            HelperDB helperDB = new HelperDB(MainActivity.this);
            ArrayList<Object> callLogPOSTModels = helperDB.getListObject("list_of_unsent_logs", CallLogPOSTModel.class);
            callLogPOSTModels.add(0, callLogPOSTModel);
            helperDB.putListObject("list_of_unsent_logs", callLogPOSTModels);

            postPhoneLog(callLogPOSTModels, callDayTime);
        }
    }

    public void postPhoneLog(final ArrayList<Object> callLogPOSTModels, final Date date) {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        if (!isEmpty(CallLogPrefs.getPostURL())) {
            Call<Void> call = apiService.sendPhoneLog(CallLogPrefs.getPostURL(), callLogPOSTModels);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    CallLogPrefs.setSentLogID("" + date.getTime());
                    Log.d("xtag", "save call log as sent - " + date.getTime());

                    ArrayList<Object> callLogPOSTModels = new ArrayList<Object>();
                    HelperDB helperDB = new HelperDB(MainActivity.this);
                    helperDB.putListObject("list_of_unsent_logs", callLogPOSTModels);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {


                    if (CallLogPrefs.getServiceRunning()) {
                        final Handler handler = new Handler();
                        runnable = new Runnable() {
                            public void run() {
                                Log.d("xtag", "resend arraylist");
                                HelperDB helperDB = new HelperDB(MainActivity.this);
                                postPhoneLog(helperDB.getListObject("list_of_unsent_logs", CallLogPOSTModel.class),
                                        date);
                            }
                        };
                        handler.postDelayed(runnable, 600000);
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

}

