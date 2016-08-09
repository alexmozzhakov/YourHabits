package com.yongchun.library.model;

import java.util.ArrayList;
import java.util.List;

public class LocalMediaFolder {
    private String name;
    private String path;
    private String firstImagePath;
    private int imageNum;
    private List<LocalMedia> images = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(final String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(final int imageNum) {
        this.imageNum = imageNum;
    }

    public List<LocalMedia> getImages() {
        return images;
    }

    public void setImages(final List<LocalMedia> images) {
        this.images = images;
    }
}
