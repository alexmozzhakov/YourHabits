package com.yongchun.library.model;

public class LocalMedia {
    private String path;
    private long duration;


    public LocalMedia(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }
}
