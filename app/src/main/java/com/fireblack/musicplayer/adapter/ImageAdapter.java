package com.fireblack.musicplayer.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.utils.ImageUtils;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int currentId=-1;
    private int wh=0;

    /**
     * 皮肤预览资源的ID数组
     * */
    public static final int[] SKIN_RESOURCES = { R.drawable.preview_bg01,
            R.drawable.preview_bg02, R.drawable.preview_bg03, R.drawable.preview_bg04,
            R.drawable.preview_bg05, R.drawable.preview_bg06 };

    public ImageAdapter(Context mContext,int currentId){
        this.mContext=mContext;
        this.currentId=currentId;

        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        wh=(int) ((outMetrics.widthPixels-(outMetrics.density*10*4))/4);
    }

    public void setCurrentId(int currentId){
        this.currentId=currentId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return SKIN_RESOURCES.length;
    }

    @Override
    public Object getItem(int position) {
        return SKIN_RESOURCES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view=null;
        if(convertView==null){
            view=new ImageView(mContext);
            view.setLayoutParams(new AbsListView.LayoutParams(wh,wh));
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else{
            view=(ImageView) convertView;
        }
        //判断是否是同一款背景
        if(position==currentId){
            view.setBackgroundDrawable(ImageUtils.createSelectedTip(mContext, SKIN_RESOURCES[position], R.drawable.skin_selected_bg_tip));
        }else{
            view.setBackgroundResource(SKIN_RESOURCES[position]);
        }
        return view;
    }

}
