package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.entity.Song;
import com.fireblack.musicplayer.service.MediaPlayerManager;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/24.
 */
public class SongWebAdapter extends BaseAdapter {
    private Context context;
    private List<Song> data;
    private int[] playerInfo = new int[2];
    private ItemListener itemListener;

    public SongWebAdapter(Context context, List<Song> data) {
        this.context = context;
        this.data = data;
        playerInfo[0] = -1;
        playerInfo[1] = -1;
    }

    public SongWebAdapter setItemListener(ItemListener itemListener){
        this.itemListener = itemListener;
        return this;
    }

    public void setPlayerInfo(int[] playerInfo){
        this.playerInfo = playerInfo;
        notifyDataSetChanged();
    }
    public void setPlayerState(int playerState){
        this.playerInfo[1] = playerState;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        if(convertView == null){
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.song_list_web,null);
            mHolder.tv_web_list_number = (TextView) convertView.findViewById(R.id.tv_web_list_number);
            mHolder.tv_web_list_top = (TextView) convertView.findViewById(R.id.tv_web_list_top);
            mHolder.tv_web_list_bottom = (TextView) convertView.findViewById(R.id.tv_web_list_bottom);
            mHolder.ibtn_web_list_download = (Button) convertView.findViewById(R.id.ibtn_web_list_download);
            convertView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        final Song song = data.get(position);
        //是否与播放时同一首
        if(song.getId() == playerInfo[0]){
            mHolder.tv_web_list_number.setText("");
            if(playerInfo[1] == MediaPlayerManager.STATE_PAUSE){
                mHolder.tv_web_list_number.setBackgroundResource(R.drawable.music_list_item_pause);
            }else if(playerInfo[1] == MediaPlayerManager.STATE_PLAYER || playerInfo[1] == MediaPlayerManager.STATE_BUFFER){
                mHolder.tv_web_list_number.setBackgroundResource(R.drawable.music_list_item_player);
            }else if(playerInfo[1] == MediaPlayerManager.STATE_OVER){
                mHolder.tv_web_list_number.setText((position+1)+"");
                mHolder.tv_web_list_number.setBackgroundResource(0);
            }
        }else {
            mHolder.tv_web_list_number.setText((position+1)+"");
            mHolder.tv_web_list_number.setBackgroundResource(0);
        }
        mHolder.tv_web_list_top.setText(song.getName());
        mHolder.tv_web_list_top.setTag(song.getId());
        mHolder.tv_web_list_bottom.setText(song.getArtist().getName());
        mHolder.tv_web_list_bottom.setTag(song.getNetUrl());

        mHolder.ibtn_web_list_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemListener != null){
                    itemListener.onDoadLoad(song);
                }
            }
        });

        mHolder.ibtn_web_list_download.setFocusable(false);
        mHolder.ibtn_web_list_download.setFocusableInTouchMode(false);

        return convertView;
    }

    public class ViewHolder{
        public TextView tv_web_list_number;
        public TextView tv_web_list_top;
        public TextView tv_web_list_bottom;
        public Button ibtn_web_list_download;
    }

    public interface ItemListener{
        void onDoadLoad(Song song);
    }
}
