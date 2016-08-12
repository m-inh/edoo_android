package com.fries.edoo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fries.edoo.R;
import com.fries.edoo.holder.AbstractHolder;
import com.fries.edoo.holder.ItemPostHolder;
import com.fries.edoo.holder.ItemWritePostHolder;
import com.fries.edoo.models.ItemBase;
import com.fries.edoo.models.ItemTimeLine;

import java.util.ArrayList;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<AbstractHolder> implements ItemPostHolder.OnClickItemPost {
    private static final String TAG = "TimelineAdapter";
    private Context mContext;
    private ArrayList<ItemBase> itemArr;
    private ArrayList<ItemBase> currentItemArr;
    private String idLop;

    public static final int BAI_DANG_BINH_THUONG = 1;
    public static final int BAI_DANG_LOC_THEO_GIAO_VIEN = 2;
    public static final int BAI_DANG_LOC_THEO_CHUA_TRA_LOI = 3;

    public int currentMode = 1;

    public TimeLineAdapter(Context context, String idLop, String keyLopType) {
        this.mContext = context;
        this.idLop = idLop;
        itemArr = new ArrayList<>();
        currentItemArr = new ArrayList<>();
    }

    public TimeLineAdapter(Context context) {
        this.mContext = context;
        itemArr = new ArrayList<>();
        currentItemArr = new ArrayList<>();
    }

    private int itemCompleteVisiblePosition;

    public void updateList(ArrayList<ItemBase> posts, int itemCompleteVisiblePosition) {
//        Toast.makeText(mContext, "update is done", Toast.LENGTH_SHORT).show();
        this.itemCompleteVisiblePosition = itemCompleteVisiblePosition;
        itemArr.clear();
        itemArr.addAll(posts);
        this.currentItemArr = posts;
        locBaiDang(currentMode);
//        Log.i(TAG, itemCompleteVisiblePosition + " position");
    }

    public void locBaiDang(int mode) {
        currentItemArr.clear();
        switch (mode) {
            case BAI_DANG_BINH_THUONG:
                currentItemArr.addAll(itemArr);
                break;
            case BAI_DANG_LOC_THEO_CHUA_TRA_LOI:
                for (int i = 0; i < itemArr.size(); i++) {
                    ItemTimeLine itemTimeLine = (ItemTimeLine) itemArr.get(i);
                    if (!itemTimeLine.isConfirmByTeacher()) {
                        currentItemArr.add(itemArr.get(i));
                    }
                }
                break;
            case BAI_DANG_LOC_THEO_GIAO_VIEN:
                for (int i = 0; i < itemArr.size(); i++) {
                    ItemTimeLine itemTimeLine = (ItemTimeLine) itemArr.get(i);
                    if (itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher")) {
                        currentItemArr.add(itemArr.get(i));
                    }
                }
                break;
        }
        currentMode = mode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
//        if(position == 0){
//            return 0;
//        }
//        else {
//            return 1;
//        }
        return 1;
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
            ItemPostHolder itemPostHolder = new ItemPostHolder(view, this);
            Log.i(TAG, "onCreateViewHolder");
            return itemPostHolder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.writepost_layout, parent, false);
            ItemWritePostHolder itemWritePostHolder = new ItemWritePostHolder(view, idLop, "");
            return itemWritePostHolder;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AbstractHolder abstractHolder, int position) {
        Log.i(TAG, position + " position");
        if(abstractHolder.getViewHolderType() == 1){
//            Log.i(TAG,"bat dau");

            //itemPostHolder.getImgAvatar();
            ItemPostHolder itemPostHolder = (ItemPostHolder) abstractHolder;
            final ItemTimeLine itemTimeLine = (ItemTimeLine) currentItemArr.get(position);
//            itemPostHolder.startAnim();
            itemPostHolder.setIdLop(idLop);
            itemPostHolder.setKeyLopType(itemTimeLine.getKeyLopType());
            itemPostHolder.setIdPost(itemTimeLine.getIdPost());
            itemPostHolder.setItemTimeLine(itemTimeLine);
            itemPostHolder.setListComment(itemTimeLine.getItemComments());
            itemPostHolder.getTxtAuthor().setText(itemTimeLine.getName());

            Log.i(TAG, "title" + itemTimeLine.getTitle());
            itemPostHolder.getTxtTitle().setText(itemTimeLine.getTitle());
            itemPostHolder.getTxtContent().setText(itemTimeLine.getContent());
            itemPostHolder.setLike(itemTimeLine.getLike());
            itemPostHolder.getTxtCountLike().setText(itemTimeLine.getLike() + "");
            if (itemTimeLine.getLike() >= 0) {
                itemPostHolder.getIvLike().setImageResource(R.mipmap.ic_up_24);
            } else {
                itemPostHolder.getIvLike().setImageResource(R.mipmap.ic_down_24);
            }

            int countCmt = itemTimeLine.getItemComments().size();
            if (countCmt == 0){
                countCmt = itemTimeLine.getCommentCount();
            }
            itemPostHolder.getTxtCountComment().setText(countCmt + "");
            itemPostHolder.getTvTimeCreateAt().setText(", " + itemTimeLine.getCreateAt());

            boolean isPostByTeacher = itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher");

            if (isPostByTeacher) {
                itemPostHolder.getIvBookmark().setVisibility(View.VISIBLE);
                itemPostHolder.getIvBookmark().setImageResource(R.mipmap.ic_bookmark_post_giangvien);
            } else if (!itemTimeLine.isConfirmByTeacher()) {
                itemPostHolder.getIvBookmark().setVisibility(View.INVISIBLE);
            } else {
                itemPostHolder.getIvBookmark().setVisibility(View.VISIBLE);
                itemPostHolder.getIvBookmark().setImageResource(R.mipmap.ic_bookmark_check);
            }

            if (itemTimeLine.isSeen()) {
                itemPostHolder.getIvSeen().setVisibility(View.INVISIBLE);
            } else {
                itemPostHolder.getIvSeen().setVisibility(View.VISIBLE);
            }

        } else {
               // ItemWritePostHolder itemWritePostHolder = (ItemWritePostHolder) abstactHolder;
        }
    }

    @Override
    public int getItemCount() {
        return currentItemArr == null ? 0 : currentItemArr.size();
    }

    @Override
    public void onClick(int position) {
//        notifyDataSetChanged();
        notifyItemChanged(position);
    }
}
