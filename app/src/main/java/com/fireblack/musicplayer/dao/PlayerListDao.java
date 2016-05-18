package com.fireblack.musicplayer.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/18.
 */
public class PlayerListDao {
    private DBHelper dbHelper;
    public PlayerListDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 全部查询
     */
    public List<String[]> searchAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select * from playerList order by date desc ", null);
        while (cursor.moveToNext()){
            String[] s = new String[3];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = cursor.getString(cursor.getColumnIndex("name"));
            s[2] = "";
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }
}
