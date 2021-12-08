package com.nd.net.upload;


import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @author cwj
 */
public class CountingRequestBody extends RequestBody {

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    protected RequestBody mDelegate;
    protected UploadProgressListener mUploadProgressListener;

    protected CountingSink mCountingSink;

    public CountingRequestBody(RequestBody delegate, UploadProgressListener uploadProgressListener) {
        this.mDelegate = delegate;
        this.mUploadProgressListener = uploadProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mCountingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(mCountingSink);
        mDelegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;
        private long contentLength = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            if (contentLength == 0)
                contentLength = contentLength();
            if (mUploadProgressListener != null) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mUploadProgressListener.updateUpload(bytesWritten, contentLength, bytesWritten >= contentLength);
                    }
                });
            }
        }
    }
}