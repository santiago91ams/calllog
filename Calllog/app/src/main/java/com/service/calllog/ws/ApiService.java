package com.service.calllog.ws;

import com.service.calllog.core.CallLogPOSTModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ghitaistrate on 01/08/2017.
 */


public interface ApiService {
    @POST("{fullUrl}")
    Call<Void> sendPhoneLog(@Path(value = "fullUrl", encoded = true) String fullUrl, @Body ArrayList<Object> phoneLog);
}

