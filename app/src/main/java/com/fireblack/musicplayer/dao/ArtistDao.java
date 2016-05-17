package com.fireblack.musicplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.entity.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/16.
 */
public class ArtistDao {
    private DBHelper dbHelper = null;

    public ArtistDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<String[]> SearchByAll(){
        List<String[]> list = new ArrayList<String[]>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from artist order by name asc", null);
        while (cursor.moveToNext()){
            String[] s = new String[3];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = cursor.getString(cursor.getColumnIndex("name"));
            s[2] = cursor.getString(cursor.getColumnIndex("picPath"));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     *判断歌手名字是否存在，存在返回_id
     */
    public int isExist(String str){
        int id = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select _id from artist where name = ?", new String[]{str});
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }

    public long add(Artist artist){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",artist.getName());
        values.put("picPath",artist.getPicPath());
        long rowid = db.insert("artist",null,values);
        db.close();
        return rowid;
    }
}
