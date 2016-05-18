package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fireblack.musicplayer.entity.DownLoadInfo;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/18.
 */
public class DownLoadListAdapter extends BaseAdapter {
    private Context context;
    private ItemListener mItemListener;
    private List<DownLoadInfo> data;

    public DownLoadListAdapter(Context context, List<DownLoadInfo> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<DownLoadInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public DownLoadListAdapter setItemListener(ItemListener mItemListener){
        this.mItemListener = mItemListener;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }

    public interface ItemListener{
        void onDelete(String url);
        void onPause(String url,int state);
    }
}
