package com.uet.fries.edoo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.uet.fries.edoo.models.ItemLop;

import java.util.ArrayList;
import java.util.Random;

public class LopAdapter extends BaseAdapter {

    private ArrayList<ItemLop> itemArr = new ArrayList<>();
    private Context mContext;
    private LayoutInflater lf;

    public LopAdapter(Context mContext) {
        this.mContext = mContext;
        lf = LayoutInflater.from(mContext);
        initData();
    }

    public void setItemArr(ArrayList<ItemLop> itemArr) {
        this.itemArr = itemArr;
        notifyDataSetChanged();
    }

    protected void initData() {
//        itemArr.add(new ItemLop("Tin hoc co so 4", "INT2204 1", "11", "Le Nguyen Khoi", 90));
//        itemArr.add(new ItemLop("Co Nhiet", "INT2204 1", "Dinh Van Chau", 90));
//        itemArr.add(new ItemLop("Tin hoc co so 1", "INT2204 1", "Le Nguyen Khoi", 90));
//        itemArr.add(new ItemLop("Xac suat thong ke", "INT2204 1", "Le Phe Do", 90));
//        itemArr.add(new ItemLop("Tin hoc co so 4", "INT2204 1", "Le Nguyen Khoi", 90));
//        itemArr.add(new ItemLop("Tin hoc co so 4", "INT2204 1", "Le Nguyen Khoi", 90));
//        itemArr.add(new ItemLop("Tin hoc co so 4", "INT2204 1", "Le Nguyen Khoi", 90));
//        itemArr.add(new ItemLop("Tin hoc co so 4", "INT2204 1", "Le Nguyen Khoi", 90));
//        itemArr.add(new ItemLop("Tin hoc co so 4", "INT2204 1", "Le Nguyen Khoi", 90));

//        resBgId.add(R.drawable.avar_item_bg_1);
//        resBgId.add(R.drawable.avar_item_bg_2);
//        resBgId.add(R.drawable.avar_item_bg_3);
//        resBgId.add(R.drawable.avar_item_bg_4);
//        resBgId.add(R.drawable.avar_item_bg_5);
    }

    @Override
    public int getCount() {
        return itemArr.size();
    }

    @Override
    public ItemLop getItem(int position) {
        return itemArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Random rand = new Random();

    private long lastTimeUpdateItem = System.currentTimeMillis();
    private static final long MIN_TIME_UPDATE = 200;
    private long timeDelay = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = lf.inflate(com.uet.fries.edoo.R.layout.item_lopmonhoc, null);
        }

        // Set Animation for Item in ListView
//        final View finalConvertView = convertView;
//        convertView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Animation myAni = AnimationUtils.loadAnimation(mContext, R.anim.anim_show_item_listview);
//                finalConvertView.startAnimation(myAni);
//            }
//        }, MIN_TIME_UPDATE*position);

        Animation myAni = AnimationUtils.loadAnimation(mContext, com.uet.fries.edoo.R.anim.anim_show_item_listview);
        convertView.startAnimation(myAni);


        TextView tvTen = (TextView) convertView.findViewById(com.uet.fries.edoo.R.id.tv_tenlopmonhoc);
        TextView tvId = (TextView) convertView.findViewById(com.uet.fries.edoo.R.id.tv_idlopmonhoc);
        TextView tvGiangVien = (TextView) convertView.findViewById(com.uet.fries.edoo.R.id.tv_giangvienlopmonhoc);
        TextView tvSoNguoi = (TextView) convertView.findViewById(com.uet.fries.edoo.R.id.tv_songuoilopmonhoc);

        tvTen.setText(itemArr.get(position).ten);
        tvId.setText(itemArr.get(position).id);
        tvGiangVien.setText(itemArr.get(position).giangVien);

        tvSoNguoi.setText("Số người: " + itemArr.get(position).soNguoi);

        return convertView;
    }
}
