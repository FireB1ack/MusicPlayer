package com.fireblack.musicplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.entity.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/16.
 */
public class AlbumDao {
    private DBHelper dbHelper = null;

    public AlbumDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<String[]> searchByAll(){
        List<String[]> list = new ArrayList<String[]>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from album order by name desc", null);
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
     *判断专辑名字是否存在，存在返回_id
     */
    public int isExist(String str){
        int id = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select _id from album where name = ?", new String[]{str});
        while (cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }

    /**
     * 添加
     */
    public long add(Album album){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",album.getName());
        values.put("picPath",album.getPicPath());
        long rowid = db.insert("album","name",values);
        db.close();
        return rowid;
    }
}
