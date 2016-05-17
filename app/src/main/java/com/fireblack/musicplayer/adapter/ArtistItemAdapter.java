package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblack.musicplayer.R;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/16.
 */
public class ArtistItemAdapter extends BaseAdapter {
    private Context context;
    private List<String[]> data;
    private int defaultIcon;

    public ArtistItemAdapter(Context context, List<String[]> data, int defaultIcon) {
        this.data = data;
        this.defaultIcon = defaultIcon;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item,null);
            mHolder.iv_list_item_icon = (ImageView) convertView.findViewById(R.id.iv_list_item_icon);
            mHolder.tv_list_item_title = (TextView) convertView.findViewById(R.id.tv_list_item_title);
            mHolder.iv_list_item_icon2 = (ImageView) convertView.findViewById(R.id.iv_list_item_icon2);
            convertView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        String picPath = data.get(position)[2];
        if (TextUtils.isEmpty(picPath)) {
            //设置为默认图片
            mHolder.iv_list_item_icon.setImageResource(defaultIcon);
        }else {
            Bitmap bitmap = BitmapFactory.decodeFile(picPath);
            //判断图片是否存在
            if(bitmap != null){
                mHolder.iv_list_item_icon.setImageBitmap(bitmap);
            }else {
                mHolder.iv_list_item_icon.setImageResource(defaultIcon);
            }
        }

        mHolder.tv_list_item_title.setText(data.get(position)[1]);
        mHolder.tv_list_item_title.setTag(data.get(position)[0]);
        mHolder.iv_list_item_icon2.setImageResource(R.drawable.playlist_sign);
        return convertView;
    }

    public class ViewHolder{
        public ImageView iv_list_item_icon;
        public TextView  tv_list_item_title;
        public ImageView iv_list_item_icon2;
    }
}
