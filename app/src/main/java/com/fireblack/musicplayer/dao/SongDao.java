package com.fireblack.musicplayer.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/14.
 * 歌曲Dao
 */
public class SongDao {
    private static DBHelper dbHelper;


    public SongDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 全部歌曲：0:id 1:文件名 2:歌手　3：文件路径 4:喜欢收藏
     */
    public static List<String[]> searchByAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A.isLike, A._id, A.displayName, B.name, " +
                "A.filePath from song as A inner join artist as B where A.artistId = B._id order by displayName desc", null);
        while(cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = Common.gainSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }
}
