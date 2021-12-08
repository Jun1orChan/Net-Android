package com.nd.net_android;


/**
 * Created by Junior on 2018/3/28.
 */

public class UploadResult {

    /**
     * id : 1196
     * file_name : 20180328093216872.jpg
     * relative_path : resource/attachment/20180328093216872.jpg
     * url : http://47.95.14.230:9998/resource/attachment/20180328093216872.jpg
     * thumb : {"relative_path":"","url":""}
     */

    private int id;
    private String file_name;
    private String relative_path;
    private String url;
    private ThumbBean thumb;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getRelative_path() {
        return relative_path;
    }

    public void setRelative_path(String relative_path) {
        this.relative_path = relative_path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ThumbBean getThumb() {
        return thumb;
    }

    public void setThumb(ThumbBean thumb) {
        this.thumb = thumb;
    }

    public static class ThumbBean {
        /**
         * relative_path :
         * url :
         */

        private String relative_path;
        private String url;

        public String getRelative_path() {
            return relative_path;
        }

        public void setRelative_path(String relative_path) {
            this.relative_path = relative_path;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
