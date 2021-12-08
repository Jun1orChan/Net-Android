package com.nd.net.download;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author cwj
 */
public class DownloadProgressResponseBody extends ResponseBody {

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private ResponseBody mResponseBody;
    private DownloadProgressListener mProgressListener;
    private BufferedSource mBufferedSource;

    public DownloadProgressResponseBody(ResponseBody responseBody,
                                        DownloadProgressListener progressListener) {
        this.mResponseBody = responseBody;
        this.mProgressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                if (null != mProgressListener) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressListener.updateDownload(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                        }
                    });
                }
                return bytesRead;
            }
        };

    }
}