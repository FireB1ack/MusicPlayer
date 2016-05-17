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

        int count = 0;
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
                    //处理歌手
                    Artist artist = song.getArtist();
                    int artist_id = artistDao.isExist(artist.getName());
                    //判断歌手名字是否在数据库已经存在，若果不存在就添加歌手名字
                    if(artist_id == -1){
                        artist_id = (int) artistDao.add(artist);
                    }
                    artist.setId(artist_id);
                    song.setArtist(artist);

                    //处理专辑
                    Album album = song.getAlbum();
                    int album_id = albumDao.isExist(album.getName());
                    if(album_id == -1){
                        album_id = (int) albumDao.add(album);
                    }
                    album.setId(album_id);
                    song.setAlbum(album);
                }else {
                    song =new Song();
                    //处理歌手，判断"未知歌手"名字是否在歌手表中已经存在
                    int unArtist_id = artistDao.isExist("未知歌手");
                    if(unArtist_id == -1){
                        unArtist_id = (int) artistDao.add(new Artist(0, "未知歌手", ""));
                    }
                    song.setArtist(new Artist(unArtist_id,"",""));

                    //处理专辑，判断"未知专辑"名字是否存在在专辑表中
                    int unAlbum_id = albumDao.isExist("未知专辑");
                    if(unAlbum_id == -1){
                        unAlbum_id = (int) albumDao.add(new Album(0, "未知专辑", ""));
                    }
                    song.setAlbum(new Album(unAlbum_id, "", ""));
                    song.setDisplayName(Common.clearDirectory(fp));
                    song.setDurationTime(-1);
                    song.setFilePath(fp);
                    song.setLyricPath(null);
                    song.setMimeType("");
                    song.setName("");
                    song.setIsNet(false);
                    song.setNetUrl(null);
                    song.setIsDownFinish(false);
                    song.setIsLike(false);
                    song.setPlayerList("#1#");
                    song.setSize(-1);
                }
                if(songDao.add(song) > 0){
                    count++;
                }
            }
        }

        Message msg = handler.obtainMessage();
        Bundle data = new Bundle();
        data.putString("path", "扫描完毕，一共" + count +"首歌曲！");
        msg.what = 1;
        msg.setData(data);
        msg.sendToTarget();

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
