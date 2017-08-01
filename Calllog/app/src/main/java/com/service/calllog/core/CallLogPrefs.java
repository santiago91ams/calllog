package com.service.calllog.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ghita on 31/07/2017.
 */

public class CallLogPrefs {

    private static final String CALL_LOG_ID = "last.sent.log";
    private static final String POST_URL = "post.url";
    private static SharedPreferences prefs;

    public CallLogPrefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void setSentLogID(String logID) {
        prefs.edit().putString(CALL_LOG_ID, logID).commit();
    }

    public static String getLastSentLogID() {
        return prefs.getString(CALL_LOG_ID, "");
    }

    public static void setPostUrl(String url){
        prefs.edit().putString(POST_URL, url).commit();
    }

    public static String getPostURL(){
        return prefs.getString(POST_URL, "");
    }

}
