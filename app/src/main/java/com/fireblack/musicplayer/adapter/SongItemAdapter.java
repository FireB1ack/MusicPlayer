package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireblack.musicplayer.R;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/14.
 */
public class SongItemAdapter extends BaseAdapter {
    private Context context;
    private List<String[]> data;
    private int[] playerInfo = new int[2];//0:playerId  1:playerState
    public SongItemAdapter(Context context,List<String[]> data) {
        this.context = context;
        this.data = data;
//        this.playerInfo = playerInfo;
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
        viewHolder.tv_song_list_item_number.setText((position+1) + "");
        viewHolder.tv_song_list_item_top.setText(d[1]);
        viewHolder.tv_song_list_item_top.setTag(d[3]);
        viewHolder.tv_song_list_item_bottom.setText(d[2]);
        viewHolder.tv_song_list_item_bottom.setTag(d[0]);

        viewHolder.ibtn_song_list_item_like.setBackgroundResource(R.drawable.dislike);
        viewHolder.ibtn_song_list_item_like.setTag(d[4]);


        return convertView;
    }
    public class ViewHolder{
        public TextView tv_song_list_item_number;
        public TextView tv_song_list_item_top;
        public TextView tv_song_list_item_bottom;
        public ImageButton ibtn_song_list_item_menu;
        public ImageButton ibtn_song_list_item_like;
    }
}
