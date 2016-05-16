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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
