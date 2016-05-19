package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fireblack.musicplayer.R;
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
        ViewHolder mHolder = null;
        if(convertView == null){
            mHolder = new ViewHolder();
            mHolder.tv_download_list_item_number = (TextView) convertView.findViewById(R.id.tv_download_list_item_number);
            mHolder.tv_download_list_item_top = (TextView) convertView.findViewById(R.id.tv_download_list_item_top);
            mHolder.tv_download_list_item_bottom = (TextView) convertView.findViewById(R.id.tv_download_list_item_bottom);
            mHolder.pb_download_list_item = (ProgressBar) convertView.findViewById(R.id.pb_download_list_item);
            mHolder.btn_download_list_item_pause = (Button) convertView.findViewById(R.id.btn_download_list_item_pause);
            mHolder.btn_download_list_item_delete = (Button) convertView.findViewById(R.id.btn_download_list_item_delete);

            convertView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public class ViewHolder{
        public TextView tv_download_list_item_number;
        public TextView tv_download_list_item_top;
        public TextView tv_download_list_item_bottom;
        public ProgressBar pb_download_list_item;
        public Button btn_download_list_item_pause;
        public Button btn_download_list_item_delete;
    }

    public interface ItemListener{
        void onDelete(String url);
        void onPause(String url,int state);
    }
}
