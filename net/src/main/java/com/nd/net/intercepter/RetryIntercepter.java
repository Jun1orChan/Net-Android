package com.nd.net.intercepter;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 重试拦截器，至少要保证目前服务器可以找到。当状态码code >= 200 && code < 300 不在此区间的时候，再次尝试请求
 * Created by Android on 2017/7/13.
 */
public class RetryIntercepter implements Interceptor {

    public static final String RETRYTIMES = "Retry-Times";

    @Override
    public Response intercept(Chain chain) throws IOException {
        int retryNum = 0;
        Request request = chain.request();
        String retryTimes = request.header(RETRYTIMES);
        Request requestNew = request.newBuilder().removeHeader(RETRYTIMES).build();
        Response response = chain.proceed(requestNew);
        if (!TextUtils.isEmpty(retryTimes)) {
            int maxRetry = 0;
            try {
                maxRetry = Integer.parseInt(retryTimes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                Log.e("TAG", String.format("正在第%d次重试...", retryNum));
                response = chain.proceed(requestNew);
            }
        }
        return response;
    }
}