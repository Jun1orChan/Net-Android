package com.nd.net.download;

/**
 * @author cwj
 */
public interface DownloadProgressListener {
    /**
     * 下载进度
     *
     * @param bytesRead     已经读取长度
     * @param contentLength 总长度
     * @param done          是否完成
     */
    void updateDownload(long bytesRead, long contentLength, boolean done);
}