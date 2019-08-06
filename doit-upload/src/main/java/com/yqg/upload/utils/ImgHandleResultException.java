package com.yqg.upload.utils;


import com.yqg.upload.common.FileStorage;

/**
 */
public class ImgHandleResultException extends RuntimeException {


    private FileStorage info;


    private byte[] stream;


    private boolean hasStoraged;


    private boolean hasThumbnail;

    public boolean isHasStoraged() {
        return hasStoraged;
    }

    public void setHasStoraged(boolean hasStoraged) {
        this.hasStoraged = hasStoraged;
    }

    public boolean isHasThumbnail() {
        return hasThumbnail;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public FileStorage getInfo() {
        return info;
    }

    public void setInfo(FileStorage info) {
        this.info = info;
    }

    public byte[] getStream() {
        return stream;
    }

    public void setStream(byte[] stream) {
        this.stream = stream;
    }
}
