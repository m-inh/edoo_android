package com.fries.edoo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.fries.edoo.R;

import java.util.ArrayList;

/**
 * Created by TooNies1810 on 3/15/16.
 */
public class ImagePostDetailAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater lf;
    private ArrayList<String> urlArr;

    public ImagePostDetailAdapter(Context mContext) {
        this.mContext = mContext;
        lf = LayoutInflater.from(mContext);

        initData();
    }

    private void initData() {
        urlArr = new ArrayList<>();
        urlArr.add("123");
        urlArr.add("123");
        urlArr.add("123");
        urlArr.add("123");
        urlArr.add("123");
    }

    @Override
    public int getCount() {
        return urlArr.size();
    }

    @Override
    public Object getItem(int position) {
        return urlArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = lf.inflate(R.layout.item_image_post_detail, null);
        }
        return convertView;
    }
}
