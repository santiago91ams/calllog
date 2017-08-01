package com.service.calllog.ws;

import android.text.TextUtils;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ghitaistrate on 01/08/2017.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://myhost/mypath/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(provideOkHttpClient(provideInterceptor(),provideCookieJar()))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient provideOkHttpClient(Interceptor interceptor, CookieManager cookieJar) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new JavaNetCookieJar(cookieJar));
        builder.interceptors().add(interceptor);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.interceptors().add(httpLoggingInterceptor);

        return builder.build();
    }

    private  static Interceptor provideInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();

                builder.addHeader("Content-Type", "application/json");

                return chain.proceed(builder.build());
            }
        };
    }


    private static CookieManager provideCookieJar() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return cookieManager;
    }
}