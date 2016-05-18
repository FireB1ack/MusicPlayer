package com.fireblack.musicplayer.entity;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/18.
 * 线程下载信息
 */
public class DownLoadInfo {
    private int id;
    private String url;
    private int fileSize;//文件大小
    private String name;
    private String artist;
    private String album;
    private String displayName;
    private String mimeType;
    private String filePath;//保存文件的路径
    private int durationTime;//播放时长
    private int completeSize;//下载进度
    private int state;//下载状态
    private int threadCount;//运行时活动的线程数量
    private List<Thread> threadInfos;//多线程信息

    public DownLoadInfo() {
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(int completeSize) {
        this.completeSize = completeSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public List<Thread> getThreadInfos() {
        return threadInfos;
    }

    public void setThreadInfos(List<Thread> threadInfos) {
        this.threadInfos = threadInfos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
