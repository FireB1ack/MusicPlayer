package com.fireblack.musicplayer.entity;

import java.io.Serializable;

/**
 * Created by ChengHao on 2016/5/17.
 * 专辑实体类序列化
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String picPath;

    public Album(){

    }

    public Album(int id, String name, String picPath) {
        this.id = id;
        this.name = name;
        this.picPath = picPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
