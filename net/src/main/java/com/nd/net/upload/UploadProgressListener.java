package com.nd.net.upload;

/**
 * Created by Junior on 2018/1/26.
 *
 * @author cwj
 */

public interface UploadProgressListener {
    /**
     * 上传进度
     *
     * @param bytesWritten  当前写入长度
     * @param contentLength 总数据长度
     * @param done          是否完成
     */
    void updateUpload(long bytesWritten, long contentLength, boolean done);
}
