package com.fireblack.musicplayer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.fireblack.musicplayer.dao.AlbumDao;
import com.fireblack.musicplayer.dao.ArtistDao;
import com.fireblack.musicplayer.dao.SongDao;
import com.fireblack.musicplayer.entity.Album;
import com.fireblack.musicplayer.entity.Artist;
import com.fireblack.musicplayer.entity.ScanData;
import com.fireblack.musicplayer.entity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/16.
 */
public class MusicManager {
    private Context context;
    public MusicManager(Context context) {
        this.context = context;
    }

    /**
     * 查询媒体库所有目录
     */
    public List<ScanData> searchByDirectory(){
        List<ScanData> list = new ArrayList<ScanData>();
        StringBuffer buffer = new StringBuffer();
        String[] prjs = {MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                prjs,null,null,MediaStore.Audio.Media.DISPLAY_NAME);
        String data = null;
        String displayName = null;
        while (cursor.moveToNext()){
            displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            data = data.replace(displayName,"").toLowerCase();//全部转换为小写并去除文件名
            if(!buffer.toString().contains(data)){
                list.add(new ScanData(data,true));
                buffer.append(data);
            }
        }
        cursor.close();
        return list;
    }



    /**
     * 扫描歌曲
     */
    public void scanMusic(String path,Handler handler){
        List<String> list = new ArrayList<String>();
        ScanMusicFilenameFilter filenameFilter = new ScanMusicFilenameFilter();
        //分割文件路径
        String[] filepath = path.split("\\#*\\#");
        for(int i = 0; i < filepath.length; i++){
            if(!filepath[i].trim().equals("")) {
                File file = new File(filepath[i]);
                File[] fs = file.listFiles(filenameFilter);
                if(fs != null){
                    for(int j = 0; j < fs.length; j++){
                        list.add(fs[j].getPath().toLowerCase());
                    }
                }
            }
        }

        SongDao songDao = new SongDao(context);
        ArtistDao artistDao = new ArtistDao(context);
        AlbumDao albumDao = new AlbumDao(context);

        //查询本库中的所有歌曲信息
        String song_filePath = songDao.getFilePathAll().toLowerCase();
        //查询在本库中不存在的所有歌曲
        HashMap<String,Song> map = searchBySong(song_filePath);

        for(int i = 0; i < list.size(); i++ ){
            String fp = list.get(i);
            if(!song_filePath.contains("#" + fp + "#")){//判断歌曲是否在本库中
                //显示扫描信息
                Message msg = handler.obtainMessage();
                Bundle data = new Bundle();
                data.putString("path",fp);
                msg.what = 0;
                msg.setData(data);
                msg.sendToTarget();

                Song song = map.get(fp);
                if(song != null){

                }
            }
        }
    }

    /**
     * 查询在本库中不存在的所有歌曲
     */
    private HashMap<String,Song> searchBySong(String filePath) {
        HashMap<String,Song> map = new HashMap<String,Song>();
        String[] proj = new String[]{MediaStore.Audio.Media.DATA,
                                    MediaStore.Audio.Media.TITLE,
                                    MediaStore.Audio.Media.ARTIST,
                                    MediaStore.Audio.Media.ALBUM,
                                    MediaStore.Audio.Media.DISPLAY_NAME,
                                    MediaStore.Audio.Media.DURATION,
                                    MediaStore.Audio.Media.MIME_TYPE,
                                    MediaStore.Audio.Media.SIZE};
        Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    proj,null,null,MediaStore.Audio.Media._ID);
        Song song = null;
        while (cursor.moveToNext()){
            String filePaths = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)).toLowerCase();
            if(!filePath.contains(filePaths)){
                song = new Song();
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                //判断是否是未知歌手
                if(TextUtils.isEmpty(artist) || artist.toLowerCase().equals("<unknown>")){
                    song.setArtist(new Artist(-1,"未知歌手",""));
                }else {
                    song.setArtist(new Artist(-1,artist.trim(),""));
                }
                //判断是否为未知专辑
                if(TextUtils.isEmpty(album)){
                    song.setAlbum(new Album(-1,"未知专辑",""));
                }else {
                    song.setAlbum(new Album(-1,album.trim(),""));
                }
                song.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setIsDownFinish(false);
                song.setDurationTime(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                song.setFilePath(filePaths);
                song.setIsLike(false);
                song.setLyricPath(null);
                song.setMimeType(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)));
                song.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setIsNet(false);
                song.setNetUrl(null);
                song.setPlayerList("#1#");//表示默认列表
                song.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                map.put(filePaths,song);
            }
        }
        return map;
    }
}
