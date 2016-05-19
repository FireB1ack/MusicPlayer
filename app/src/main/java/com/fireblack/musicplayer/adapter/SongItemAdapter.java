package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.service.MediaPlayerManager;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/14.
 *
 * data：0:歌曲id，1:上标题，2：下标题，3:文件路径，4：最爱
 */
public class SongItemAdapter extends BaseAdapter {
    private Context context;
    private List<String[]> data;
    private int[] playerInfo = new int[2];//0:playerId  1:playerState
    private ItemListener mItemListener;
    public SongItemAdapter(Context context,List<String[]> data,int[] playerInfo) {
        this.context = context;
        this.data = data;
        this.playerInfo = playerInfo;
    }
    public SongItemAdapter setItemListener(ItemListener itemListener){
        this.mItemListener = itemListener;
        return this;
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

    public void setPlayerInfo(int[] playerInfo){
        this.playerInfo = playerInfo;
        notifyDataSetChanged();
    }
    public void setPlayerState(int playerState){
        this.playerInfo[1] = playerState;
        notifyDataSetChanged();
    }
    public void deleteItem(int position){
        data.remove(position);
        notifyDataSetChanged();
    }
    public int[] getPlayerId(){
        return playerInfo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.song_list_item,null);
            viewHolder.tv_song_list_item_number = (TextView) convertView.findViewById(R.id.tv_song_list_item_number);
            viewHolder.tv_song_list_item_top = (TextView) convertView.findViewById(R.id.tv_song_list_item_top);
            viewHolder.tv_song_list_item_bottom = (TextView) convertView.findViewById(R.id.tv_song_list_item_bottom);
            viewHolder.ibtn_song_list_item_menu = (ImageButton) convertView.findViewById(R.id.ibtn_song_list_item_menu);
            viewHolder.ibtn_song_list_item_like = (ImageButton) convertView.findViewById(R.id.ibtn_song_list_item_like);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String[] d = data.get(position);
        //判断是否与播放歌曲是同一首
        if(Integer.valueOf(d[0]) == (playerInfo[0])){
            viewHolder.tv_song_list_item_number.setText("");
            //暂停
            if(playerInfo[1] == MediaPlayerManager.STATE_PAUSE){
                viewHolder.tv_song_list_item_number.setBackgroundResource(R.drawable.music_list_item_pause);
            }//播放
            else if(playerInfo[1] == MediaPlayerManager.STATE_PLAYER || playerInfo[1] == MediaPlayerManager.STATE_PREPARE){
                viewHolder.tv_song_list_item_number.setBackgroundResource(R.drawable.music_list_item_player);
            }else if(playerInfo[1] == MediaPlayerManager.STATE_OVER){
                viewHolder.tv_song_list_item_number.setText((position+1) + "");
                viewHolder.tv_song_list_item_number.setBackgroundResource(0);
            }
        }else {
            viewHolder.tv_song_list_item_number.setText((position + 1) + "");
            viewHolder.tv_song_list_item_number.setBackgroundResource(0);
        }

        viewHolder.tv_song_list_item_top.setText(d[1]);
        viewHolder.tv_song_list_item_top.setTag(d[3]);
        viewHolder.tv_song_list_item_bottom.setText(d[2]);
        viewHolder.tv_song_list_item_bottom.setTag(d[0]);

        //判断是否是最爱
        if(d[4].equals("1")){
            viewHolder.ibtn_song_list_item_like.setBackgroundResource(R.drawable.like);
        }else {
            viewHolder.ibtn_song_list_item_like.setBackgroundResource(R.drawable.dislike);
        }
        viewHolder.ibtn_song_list_item_like.setTag(d[4]);
        viewHolder.ibtn_song_list_item_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListener != null) {
                    mItemListener.onLikeClick(Integer.valueOf(d[0]), v, position);
                }
            }
        });

        viewHolder.ibtn_song_list_item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListener != null) {
                    mItemListener.onMenuClick(Integer.valueOf(d[0]),d[1],d[3],position);
                }
            }
        });
        viewHolder.ibtn_song_list_item_like.setFocusable(false);
        viewHolder.ibtn_song_list_item_like.setFocusableInTouchMode(false);

        viewHolder.ibtn_song_list_item_menu.setFocusable(false);
        viewHolder.ibtn_song_list_item_menu.setFocusableInTouchMode(false);
        return convertView;
    }
    public class ViewHolder{
        public TextView tv_song_list_item_number;
        public TextView tv_song_list_item_top;
        public TextView tv_song_list_item_bottom;
        public ImageButton ibtn_song_list_item_menu;
        public ImageButton ibtn_song_list_item_like;
    }

    public interface ItemListener{
        void onLikeClick(int id,View view,int position);
        void onMenuClick(int id,String text,String path,int position);
    }
}
