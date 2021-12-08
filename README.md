# Net-Android
对于OkHttp3的扩展：上传、下载监听器<br>

1.文件上传（单文件、多文件批量提交）和下载的进度（UI线程）；<br>
具体使用见工程示例 <br>
2.抽离RxJava2、Retrofit2，如果需要使用，自行在宿主中加入。

# 使用

1.

```groovy
    implementation 'com.github.Jun1orChan:Net-Android:1.1.0'
```

# 示例，仅给出RxJava2 + Retrofit2的用法

1、下载：
``` java

        public ApiService getDownLoadApiService(DownloadProgressListener listener) {
            DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://strongmobile.b0.upaiyun.com")
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            mDownLoadApiService = retrofit.create(ApiService.class);
            return mDownLoadApiService;
        }

      Flowable.just("http://lc-n7tgcyqf.cn-n1.lcfile.com/XkVpOeRClNGd6RngfWScESB")
                      .flatMap(new Function<String, Publisher<ResponseBody>>() {
                          @Override
                          public Publisher<ResponseBody> apply(@NonNull String s) throws Exception {
                              return ApiManager.getInstance().getDownLoadApiService(MainActivity.this)
                                      .download(s);
                          }
                      })
                      .subscribeOn(Schedulers.io())
                      .map(new Function<ResponseBody, File>() {
                          @Override
                          public File apply(@NonNull ResponseBody responseBody) throws Exception {
                              File file = new File(StorageUtil.getOwnCacheDirectory(MainActivity.this, "image", true), "test.jpg");
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
```

2、上传单文件：

``` java

    Flowable.just("http://47.95.14.230:9998/api/v1/file/upload?is_sys_attachment=true")
                    .flatMap(new Function<String, Publisher<List<UploadResult>>>() {
                        @Override
                        public Publisher<List<UploadResult>> apply(@NonNull String s) throws Exception {
                            File file = new File(StorageUtil.getOwnCacheDirectory(MainActivity.this, "image", true), "test.jpg");
                            Log.e("TAG", file.exists() + "==============" + file.getAbsolutePath());
                            CountingRequestBody resquestBody =
                                    new CountingRequestBody(RequestBody.create(MediaType.parse("multipart/form-data"), file), MainActivity.this);
                            return ApiManager.getInstance().getApiService().upload(s,
                                    MultipartBody.Part.createFormData("image", file.getName(), resquestBody));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<UploadResult>>() {
                        @Override
                        public void accept(List<UploadResult> uploadResultList) throws Exception {
                            Log.e("TAG", JSON.toJSONString(uploadResultList));
                            mProgressDialog.cancel();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mProgressDialog.cancel();
                            throwable.printStackTrace();
                        }
                    });
```

3、上传多文件：<br>

```

       Flowable.just("http://202.109.200.36:9028/webapi/api/v1/slgc/public/annexinfo-upload")
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
```

# 混淆

参考OkHttp3


## 版本记录

### 1.1.0
- 最小兼容版本改为：21
- 适配Android 12

### 1.0.0
- 初始版本

