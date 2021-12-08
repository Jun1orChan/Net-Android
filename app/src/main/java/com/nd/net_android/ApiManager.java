package com.nd.net_android;

import com.istrong.net_android.BuildConfig;
import com.nd.net.download.DownloadProgressInterceptor;
import com.nd.net.download.DownloadProgressListener;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Junior on 2017/10/16.
 */

public class ApiManager {
    private ApiService mApiService;
    private ApiService mDownLoadApiService;
    private static ApiManager mApiManager;

    public synchronized static ApiManager getInstance() {
        if (mApiManager == null) {
            mApiManager = new ApiManager();
        }
        return mApiManager;
    }

    public ApiService getDownLoadApiService(DownloadProgressListener listener) {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(interceptor);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://strongmobile.b0.upaiyun.com")
                .client(clientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mDownLoadApiService = retrofit.create(ApiService.class);
        return mDownLoadApiService;
    }

    public ApiService getApiService() {
        if (mApiService == null) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true);
            if (BuildConfig.DEBUG) {
                clientBuilder.addInterceptor(logInterceptor);
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://strongmobile.b0.upaiyun.com")
                    .client(clientBuilder.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mApiService = retrofit.create(ApiService.class);
        }
        return mApiService;
    }
}
