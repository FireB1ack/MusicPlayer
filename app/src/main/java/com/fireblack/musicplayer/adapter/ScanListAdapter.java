package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.entity.ScanData;

import java.util.List;

/**
 * Created by ChengHao on 2016/5/16.
 */
public class ScanListAdapter extends BaseAdapter {
    private Context context;
    private List<ScanData> data;
    private String path = "";

    public ScanListAdapter(Context context, List<ScanData> data) {
        this.context = context;
        this.data = data;
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
        final String filePath = data.get(position).getFilePath();
        if(convertView == null){
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.scan_list_item,null);
            mHolder.iv_scan_item_icon = (ImageView) convertView.findViewById(R.id.iv_scan_item_icon);
            mHolder.tv_scan_item_title = (TextView) convertView.findViewById(R.id.tv_scan_item_title);
            mHolder.cb_scan_item = (CheckBox) convertView.findViewById(R.id.cb_scan_item);
            convertView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.iv_scan_item_icon.setImageResource(R.drawable.directory_icon);
        mHolder.tv_scan_item_title.setText(filePath);
        mHolder.cb_scan_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //通过点击CHeckBox选择是否添加路径
                if(isChecked){
                    if(path.toString().indexOf("#"+filePath+"#") == -1){
                        path = "#"+filePath+"#";
                    }else {
                        path = path.replace("#"+filePath+"#","");
                    }
                }
            }
        });
        mHolder.cb_scan_item.setChecked(data.get(position).isChecked());
        return convertView;
    }

    private class ViewHolder{
        public ImageView iv_scan_item_icon;
        public TextView tv_scan_item_title;
        public CheckBox cb_scan_item;
    }
}
