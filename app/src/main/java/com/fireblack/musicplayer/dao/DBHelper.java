package com.fireblack.musicplayer.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ChengHao on 2016/5/14.
 */
public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context) {
        super(context, "musicplayer.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建歌曲表
        db.execSQL("CREATE TABLE song(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "albumId integer," +
                "artistId integer," +
                "name nvarchar(100)," +
                "displayName nvarchar(100)," +
                "netUrl nvarchar(500)," +
                "durationTime integer," +
                "size integer," +
                "isLike integer," +
                "lyricPath nvarchar(300)," +
                "filePath nvarchar(300)," +
                "playerList nvarchar(500)," +
                "isNet integer," +
                "mimeType nvarchar(50)," +
                "isDownFinish integer)");
        //创建歌手表
        db.execSQL("CREATE TABLE artist(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name nvarchar(100)," +
                "picPath nvarchar(300))");
        //创建专辑表
        db.execSQL("CREATE TABLE album(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name nvarchar(100)," +
                "picPath nvarchar(300))");
        //创建播放列表的表
        db.execSQL("CREATE TABLE playerList(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name nvarchar(100)," +
                "date integer)");

        //创建下载信息表
        db.execSQL("CREATE TABLE downLoadInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "url nvarchar(300)," +
                "name nvarchar(300), " +
                "artist nvarchar(100), " +
                "album nvarchar(100), " +
                "displayName nvarchar(100), " +
                "filePath nvarchar(300), " +
                "mimeType nvarchar(100), " +
                "durationTime integer, " +
                "completeSize integer, " +
                "fileSize integer)");

        //多线程下载-每一个线程信息表
        db.execSQL("CREATE TABLE threadInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "startPosition integer, " +
                "endPosition integer, " +
                "completeSize integer, " +
                "downLoadInfoId integer)");

        //添加默认列表
        db.execSQL("insert into playerList(name,date) " +
                "values('默认列表'," + System.currentTimeMillis() + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //删除表
        db.execSQL("drop table if exists song");
        db.execSQL("drop table if exists artist");
        db.execSQL("drop table if exists album");
        db.execSQL("drop table if exists playerList");
        db.execSQL("drop table if exists downLoadInfo");
        db.execSQL("drop table if exists threadInfo");
    }
}
