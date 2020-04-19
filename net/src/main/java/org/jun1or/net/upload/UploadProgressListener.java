package org.jun1or.net.upload;

/**
 * Created by Junior on 2018/1/26.
 */

public interface UploadProgressListener {
    void updateUpload(long bytesWritten, long contentLength, boolean done);
}
