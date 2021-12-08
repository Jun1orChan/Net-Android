package com.nd.net_android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import com.istrong.net_android.R;
import com.nd.net.download.DownloadProgressListener;
import com.nd.net.upload.CountingRequestBody;
import com.nd.net.upload.UploadProgressListener;
import com.nd.util.FileUtil;
import com.nd.util.StorageUtil;


import org.reactivestreams.Publisher;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class NetActivity extends AppCompatActivity implements View.OnClickListener,
        DownloadProgressListener, UploadProgressListener {

    private Button mBtnDownLoad;
    private Button mBtnUpload;
    private Disposable mDownLoadDisposable;
    private Disposable mUploadDisposable;
    private ProgressDialog mProgressDialog;
    private Disposable mMultiFileUploadDisposable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        mBtnDownLoad = (Button) findViewById(R.id.btnDownload);
        mBtnDownLoad.setOnClickListener(this);
        mBtnUpload = (Button) findViewById(R.id.btnUpload);
        mBtnUpload.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        findViewById(R.id.btnMultiFileUpload).setOnClickListener(this);
        findViewById(R.id.btnHeadRequest).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnDownload:
                downLoad();
                break;
            case R.id.btnUpload:
                upload();
                break;
            case R.id.btnMultiFileUpload:
                multiFileUpload();
                break;
            case R.id.btnHeadRequest:
                headRequest();
                break;
        }
    }

    private void headRequest() {
        Flowable.just("http://www.baidu.com")
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, Publisher<Response<Void>>>() {
                    @Override
                    public Publisher<retrofit2.Response<Void>> apply(String s) throws Exception {
                        return ApiManager.getInstance().getApiService().headRequest(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<Void>>() {
                    @Override
                    public void accept(retrofit2.Response response) throws Exception {
                        Set<String> names = response.headers().names();
                        Iterator<String> iterator = names.iterator();
                        while (iterator.hasNext()) {
                            String head = iterator.next();
                            response.headers().values(head);
                            Log.e("TAG", head + ":" + response.headers().values(head).get(0));
                        }
//                        Log.e("TAG", "body:" + response.raw().body().string());
                        Log.e("TAG", "message:" + response.body());
                        Toast.makeText(getApplicationContext(), response.code() + "==", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    private void multiFileUpload() {
        if (mMultiFileUploadDisposable != null) {
            mMultiFileUploadDisposable.dispose();
        }
        mProgressDialog.show();
        mMultiFileUploadDisposable = Flowable.just("http://202.109.200.36:9028/webapi/api/v1/slgc/public/annexinfo-upload")
                .flatMap(new Function<String, Publisher<ResponseBody>>() {
                    @Override
                    public Publisher<ResponseBody> apply(@NonNull String s) throws Exception {
                        return ApiManager.getInstance().getApiService().uploadMultiFile(s, getMultipartBody());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Log.e("TAG", "===" + responseBody.string());
                        mProgressDialog.cancel();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mProgressDialog.cancel();
                        throwable.printStackTrace();
                    }
                });
    }

    private RequestBody getMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        File file = new File(StorageUtil.getCacheDirectory(this, "file"), "activity_slidingdrawerlayout.apk");
        Log.e("TAG", file.exists() + "==============" + file.getAbsolutePath());
        RequestBody resquestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("file", file.getName(), resquestBody);
        builder.addFormDataPart("file", file.getName(), resquestBody);
        builder.addFormDataPart("file", file.getName(), resquestBody);
        builder.addFormDataPart("file", file.getName(), resquestBody);
        builder.addFormDataPart("file", file.getName(), resquestBody);
        builder.addFormDataPart("file", file.getName(), resquestBody);
        builder.setType(MultipartBody.FORM);

        MultipartBody multipartBody = builder.build();
        CountingRequestBody countingRequestBody = new CountingRequestBody(multipartBody, this);
        return countingRequestBody;
    }

    //http://47.95.14.230:9998/api/v1/file/upload?is_sys_attachment=true
    private void upload() {
        if (mUploadDisposable != null) {
            mUploadDisposable.dispose();
        }
        mProgressDialog.show();
        mUploadDisposable = Flowable.just("http://47.95.14.230:9998/api/v1/file/upload?is_sys_attachment=true")
                .flatMap(new Function<String, Publisher<List<UploadResult>>>() {
                    @Override
                    public Publisher<List<UploadResult>> apply(@NonNull String s) throws Exception {
                        File file = new File(StorageUtil.getCacheDirectory(NetActivity.this, "file"), "activity_slidingdrawerlayout.apk");
                        Log.e("TAG", file.exists() + "==============" + file.getAbsolutePath());
                        CountingRequestBody resquestBody =
                                new CountingRequestBody(RequestBody.create(MediaType.parse("multipart/form-data"), file), NetActivity.this);
                        return ApiManager.getInstance().getApiService().upload(s,
                                MultipartBody.Part.createFormData("file", file.getName(), resquestBody));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UploadResult>>() {
                    @Override
                    public void accept(List<UploadResult> uploadResultList) throws Exception {
                        Log.e("TAG", new Gson().toJson(uploadResultList));
                        mProgressDialog.cancel();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mProgressDialog.cancel();
                        throwable.printStackTrace();
                    }
                });
    }

    private void downLoad() {
        if (mDownLoadDisposable != null && !mDownLoadDisposable.isDisposed()) {
            mDownLoadDisposable.dispose();
        }
        mDownLoadDisposable = Flowable.just("http://www.istrong.cn:8088/pda/pdaupdatewebservice/Android_apk/fjfxt_v2_2.21_2018090502.apk")
                .flatMap(new Function<String, Publisher<ResponseBody>>() {
                    @Override
                    public Publisher<ResponseBody> apply(@NonNull String s) throws Exception {
                        return ApiManager.getInstance().getDownLoadApiService(NetActivity.this)
                                .download(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(@NonNull ResponseBody responseBody) throws Exception {
                        File file = new File(StorageUtil.getCacheDirectory(NetActivity.this, "file"), "activity_slidingdrawerlayout.apk");
                        FileUtil.writeFile(responseBody.byteStream(), file);
                        return file;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        Log.e("TAG", "======" + file.getAbsolutePath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void updateDownload(final long bytesRead, final long contentLength, final boolean done) {
        if (!done) {
            mBtnDownLoad.setText("bytesRead:" + bytesRead + "===" + "contentLength:" + contentLength);
        } else {
            mBtnDownLoad.setText("DOWNLOAD");
        }
    }

    @Override
    public void updateUpload(long bytesWritten, long contentLength, boolean done) {
        Log.e("TAG", "bytesRead:" + bytesWritten + "===" + "contentLength:" + contentLength + "===" + "done:" + done);
        //实际使用，可以判断这个值是否有+1，避免频繁更新UI，造成性能损耗
        Log.e("TAG", (int) (bytesWritten * 100 / contentLength) + "");
        mProgressDialog.setProgress((int) (bytesWritten * 100 / contentLength));
    }
}
