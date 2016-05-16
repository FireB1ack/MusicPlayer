package com.fireblack.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChengHao on 2016/5/17.
 * 歌曲序列化
 */
public class Song implements Parcelable {
    private int id;
    private Artist artist;
    private Album album;
    private String name;//歌曲名称，无后缀名
    private String displayName;//包含后缀名的文件名称
    private String netUrl;//网络路径
    private int durationTime;//播放时间
    private int size;
    private boolean isLike;
    private String lyricPath;//歌词路径
    private String filePath;
    private String playerList;//播放列表的id
    private boolean isNet;//是否是网络音乐
    private String mimeType;//MIME类型
    private boolean isDownFinish;

    public Song(){

    }

    public Song(Album album, Artist artist, String displayName, int durationTime, String filePath, int id, boolean isDownFinish, boolean isLike, boolean isNet, String lyricPath, String mimeType, String name, String netUrl, String playerList, int size) {
        this.album = album;
        this.artist = artist;
        this.displayName = displayName;
        this.durationTime = durationTime;
        this.filePath = filePath;
        this.id = id;
        this.isDownFinish = isDownFinish;
        this.isLike = isLike;
        this.isNet = isNet;
        this.lyricPath = lyricPath;
        this.mimeType = mimeType;
        this.name = name;
        this.netUrl = netUrl;
        this.playerList = playerList;
        this.size = size;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDownFinish() {
        return isDownFinish;
    }

    public void setIsDownFinish(boolean isDownFinish) {
        this.isDownFinish = isDownFinish;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean isNet() {
        return isNet;
    }

    public void setIsNet(boolean isNet) {
        this.isNet = isNet;
    }

    public String getLyricPath() {
        return lyricPath;
    }

    public void setLyricPath(String lyricPath) {
        this.lyricPath = lyricPath;
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

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public String getPlayerList() {
        return playerList;
    }

    public void setPlayerList(String playerList) {
        this.playerList = playerList;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeSerializable(artist);
        dest.writeSerializable(album);
        dest.writeString(name);
        dest.writeString(displayName);
        dest.writeString(netUrl);
        dest.writeInt(durationTime);
        dest.writeInt(size);
        dest.writeBooleanArray(new boolean[]{isLike,isNet,isDownFinish});
        dest.writeString(lyricPath);
        dest.writeString(filePath);
        dest.writeString(playerList);
        dest.writeString(mimeType);
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){

        @Override
        public Song createFromParcel(Parcel source) {
            Song song = new Song();
            song.id = source.readInt();
            song.artist = (Artist) source.readSerializable();
            song.album = (Album) source.readSerializable();
            song.name = source.readString();
            song.displayName = source.readString();
            song.netUrl = source.readString();
            song.durationTime = source.readInt();
            song.size = source.readInt();
            boolean[] bools = new boolean[3];
            song.isLike = bools[0];
            song.isNet = bools[1];
            song.isDownFinish = bools[2];
            source.readBooleanArray(bools);
            song.lyricPath = source.readString();
            song.filePath = source.readString();
            song.playerList = source.readString();
            song.mimeType = source.readString();
            return song;
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
