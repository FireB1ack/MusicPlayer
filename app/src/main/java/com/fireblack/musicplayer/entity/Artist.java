package com.fireblack.musicplayer.entity;

import java.io.Serializable;

/**
 * Created by ChengHao on 2016/5/17.
 *歌手实体类序列化
 */
public class Artist implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id; //id
    private String name; //歌手名字
    private String picPath; //歌手图片

    public Artist(){

    }

    public Artist(int id, String name, String picPath) {
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
