package org.jun1or.net.download;

public interface DownloadProgressListener {
    void updateDownload(long bytesRead, long contentLength, boolean done);
}