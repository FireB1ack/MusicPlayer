package com.fireblack.musicplayer.entity;

/**
 * Created by ChengHao on 2016/5/16.
 * 扫描数据实体类
 */
public class ScanData {
    private String filePath;
    private boolean isChecked;

    public ScanData(){

    }

    public ScanData(String filePath,boolean isChecked) {
        this.isChecked = isChecked;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
