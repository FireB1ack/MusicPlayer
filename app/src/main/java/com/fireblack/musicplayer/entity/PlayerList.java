package com.fireblack.musicplayer.entity;

import java.io.Serializable;

/**
 * Created by ChengHao on 2016/5/26.
 */
public class PlayerList implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private long date;//添加日期

    public PlayerList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public PlayerList(){

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
