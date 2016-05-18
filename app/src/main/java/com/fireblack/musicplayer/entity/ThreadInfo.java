package com.fireblack.musicplayer.entity;

/**
 * Created by ChengHao on 2016/5/18.
 * 子线程信息实体类
 */
public class ThreadInfo {
    private int id;
    private int downLoadInfoId;//downLoadInfo id
    private int startPosition;//开始下载的大小
    private int endPosition;//结束下载的大小
    private int completeSize;//已经下载的大小

    public ThreadInfo() {
    }

    public int getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(int completeSize) {
        this.completeSize = completeSize;
    }

    public int getDownLoadInfoId() {
        return downLoadInfoId;
    }

    public void setDownLoadInfoId(int downLoadInfoId) {
        this.downLoadInfoId = downLoadInfoId;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
}
