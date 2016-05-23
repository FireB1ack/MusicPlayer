package com.fireblack.musicplayer.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.entity.Album;
import com.fireblack.musicplayer.entity.Artist;
import com.fireblack.musicplayer.entity.Song;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/24.
 * 网络音乐解析XML
 */
public class XmlUtil {
    public static List<Song> parseWebSong(Context context){
        List<Song> data = null;
        Song song = null;
        XmlResourceParser parser = context.getResources().getXml(R.xml.web_song);
        try {
            int eventTYpe = parser.getEventType();
            //读取文档直至结束END_DOCUMENT
            while (eventTYpe != XmlResourceParser.END_DOCUMENT){
                switch (eventTYpe){
                    case XmlResourceParser.START_DOCUMENT:
                        data = new ArrayList<Song>();
                        break;
                    case XmlResourceParser.START_TAG:
                        if(parser.getName().equals("song")){
                            song = new Song();
                            song.setId(parser.getAttributeIntValue(0,0));
                        }else if(parser.getName().equals("name")){
                            song.setName(parser.nextText());
                        }else if(parser.getName().equals("artist")){
                            song.setArtist(new Artist(0,parser.nextText(),null));
                        }else if(parser.getName().equals("album")){
                            song.setAlbum(new Album(0,parser.nextText(),null));
                        }else if(parser.getName().equals("displayName")){
                            song.setDisplayName(parser.nextText());
                        }else if(parser.getName().equals("mimeType")){
                            song.setMimeType(parser.nextText());
                        }else if(parser.getName().equals("netUrl")){
                            song.setNetUrl(parser.nextText());
                        }else if(parser.getName().equals("durationTime")){
                            song.setDurationTime(Integer.valueOf(parser.nextText()));
                        }else if(parser.getName().equals("size")){
                            song.setSize(Integer.valueOf(parser.nextText()));
                        }
                        break;
                    //读取文档结束标签
                    case XmlResourceParser.END_TAG:
                        if(parser.getName().equals("song")){
                            data.add(song);
                            song=null;
                        }
                        break;
                    default:
                        break;
                }
                //读取一首歌
                eventTYpe=parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
