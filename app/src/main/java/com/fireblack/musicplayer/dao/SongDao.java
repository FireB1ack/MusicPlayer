package com.fireblack.musicplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.entity.Album;
import com.fireblack.musicplayer.entity.Artist;
import com.fireblack.musicplayer.entity.Song;
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
            s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查询本库中的所有歌曲信息,以便在扫描歌曲时不添加已经在本库中存在的歌曲
     * 返回歌曲路径用"#[String]#"分隔
     */
    public String getFilePathAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuffer sb = new StringBuffer();
        Cursor cursor = db.rawQuery("select filePath from song order by _id desc", null);
        while (cursor.moveToNext()){
            sb.append("#").append(cursor.getString(cursor.getColumnIndex("filePath"))).append("#");
        }
        cursor.close();
        db.close();
        return sb.toString();
    }

    /**
     * 添加歌曲
     */
    public long add(Song song){
        SQLiteDatabase db= dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("displayName", song.getDisplayName());
        values.put("filePath", song.getFilePath());
        values.put("lyricPath", song.getLyricPath());
        values.put("mimeType", song.getMimeType());
        values.put("name", song.getName());
        values.put("albumId", song.getAlbum().getId());
        values.put("netUrl", song.getNetUrl());
        values.put("durationTime", song.getDurationTime());
        values.put("size", song.getSize());
        values.put("artistId", song.getArtist().getId());
        values.put("playerList", song.getPlayerList());
        values.put("isDownFinish", song.isDownFinish());
        values.put("isLike", song.isLike());
        values.put("isNet", song.isNet());
        long rowid = db.insert("song", "name", values);
        db.close();
        return rowid;

    }

    /**
     * 获取记录总数
     */
    public int getCount(){
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from song", null);
        while (cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 查询全部歌曲
     */
    public List<Song> searchAll(){
        return commonSearch("",null);
    }

    /**
     * @param str
     * @param parameter 查询参数
     * 通用查询
     */
    public List<Song> commonSearch(String str, String[] parameter) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Song> list = new ArrayList<Song>();
        Song song = null;
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name as Bname, " +
                "C.name as Cname, A.name as Aname, " +
                "A.albumId, A.artistId, C.picPath as CpicPath, " +
                "B.picPath as BpicPath, A.filePath, " +
                "A.durationTime from song as A inner join artist as B on " +
                "A.artistId=B._id inner join album as C on " +
                "A.albumId=C._id" + "" + str + "order by displayName desc", parameter);
        while(cursor.moveToNext()){
            song = new Song();
            song.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
            song.setArtist(new Artist(cursor.getInt(cursor.getColumnIndex("artistId")),
                    cursor.getString(cursor.getColumnIndex("Bname")),
                    cursor.getString(cursor.getColumnIndex("BpicPath"))));
            song.setAlbum(new Album(cursor.getInt(cursor.getColumnIndex("albumId")),
                    cursor.getString(cursor.getColumnIndex("Cname")),
                    cursor.getString(cursor.getColumnIndex("CpicPath"))));
            song.setName(cursor.getString(cursor.getColumnIndex("Aname")));
            song.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            song.setDurationTime(cursor.getInt(cursor.getColumnIndex("durationTime")));
            list.add(song);
        }
        cursor.close();
        db.close();
        return list;
    }


}
