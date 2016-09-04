package com.yongchun.library.model;

public class LocalMedia {
    private final String path;
    private long duration;


    public LocalMedia(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
