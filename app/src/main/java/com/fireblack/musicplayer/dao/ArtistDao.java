package com.fireblack.musicplayer.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}
