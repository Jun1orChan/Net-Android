package com.nd.net_android;


import java.util.List;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Junior on 2017/10/16.
 */

public interface ApiService {

    @Streaming
    @GET
    Flowable<ResponseBody> download(@Url String url);

    @Multipart
    @POST
    Flowable<List<UploadResult>> upload(@Url String url, @Part MultipartBody.Part file);

    //https://blog.csdn.net/jdsjlzx/article/details/51649382 只能用@Body,因为@Body中如果参数是RequestBody，
    // 则不会包装成MultipartBody，否则进行了新的包装，导致文件流信息出错，可抓包查看
//    @Multipart
    @POST
    Flowable<ResponseBody> uploadMultiFile(@Url String url, @Body RequestBody multipartBody);


    @HEAD
    Flowable<retrofit2.Response<Void>> headRequest(@Url String url);
}
