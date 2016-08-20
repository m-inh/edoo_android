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
public class TimeLineAdapter extends RecyclerView.Adapter<AbstractHolder> {
    private static final String TAG = "TimelineAdapter";
    private Context mContext;
    private ArrayList<ItemBase> itemArr;
    private ArrayList<ItemBase> currentItemArr;
    private String idLop;

    public static final int BAI_DANG_BINH_THUONG = 1;
    public static final int BAI_DANG_LOC_THEO_GIAO_VIEN = 2;
    public static final int BAI_DANG_LOC_THEO_CHUA_TRA_LOI = 3;
    public static final int BAI_DANG_CHUA_DOC = 4;

    public int currentMode = BAI_DANG_BINH_THUONG;

    public TimeLineAdapter(Context context, String idLop) {
        this.mContext = context;
        this.idLop = idLop;
        itemArr = new ArrayList<>();
        currentItemArr = new ArrayList<>();
    }

    public void updateList(ArrayList<ItemBase> posts) {
        itemArr.clear();
        itemArr.addAll(posts);
        this.currentItemArr = posts;
        locBaiDang(currentMode);
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
                    if (!itemTimeLine.isSolve()) {
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
            case BAI_DANG_CHUA_DOC:
                for (int i = 0; i < itemArr.size(); i++) {
                    ItemTimeLine itemTimeLine = (ItemTimeLine) itemArr.get(i);
                    if (!itemTimeLine.isSeen()) {
                        currentItemArr.add(itemArr.get(i));
                    }
                }
                break;
        }
        currentMode = mode;
        notifyDataSetChanged();
    }

    public void refreshList(){
        currentItemArr.clear();
        switch (currentMode) {
            case BAI_DANG_BINH_THUONG:
                currentItemArr.addAll(itemArr);
                break;
            case BAI_DANG_LOC_THEO_CHUA_TRA_LOI:
                for (int i = 0; i < itemArr.size(); i++) {
                    ItemTimeLine itemTimeLine = (ItemTimeLine) itemArr.get(i);
                    if (!itemTimeLine.isSolve()) {
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
            case BAI_DANG_CHUA_DOC:
                for (int i = 0; i < itemArr.size(); i++) {
                    ItemTimeLine itemTimeLine = (ItemTimeLine) itemArr.get(i);
                    if (!itemTimeLine.isSeen()) {
                        currentItemArr.add(itemArr.get(i));
                    }
                }
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        return 1;
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ItemPostHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AbstractHolder abstractHolder, int position) {
        ItemPostHolder itemPostHolder = (ItemPostHolder) abstractHolder;
        final ItemTimeLine itemTimeLine = (ItemTimeLine) currentItemArr.get(currentItemArr.size() - 1 - position);
        itemPostHolder.setIdLop(idLop);
        itemPostHolder.setKeyLopType(itemTimeLine.getKeyLopType());
        itemPostHolder.setIdPost(itemTimeLine.getIdPost());
        itemPostHolder.setItemTimeLine(itemTimeLine);
        itemPostHolder.setListComment(itemTimeLine.getItemComments());
        itemPostHolder.getTxtAuthor().setText(itemTimeLine.getName());
        itemPostHolder.getTxtTitle().setText(itemTimeLine.getTitle());
        itemPostHolder.getTxtContent().setText(itemTimeLine.getDescription());
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

        itemPostHolder.getIvBookmark().setVisibility(View.INVISIBLE);

        boolean isPostByTeacher = itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher");

        if (itemTimeLine.isSolve()){
            itemPostHolder.getIvBookmark().setVisibility(View.VISIBLE);
            itemPostHolder.getIvBookmark().setImageResource(R.mipmap.ic_bookmark_check);
        }
        if (isPostByTeacher) {
            itemPostHolder.getIvBookmark().setVisibility(View.VISIBLE);
            itemPostHolder.getIvBookmark().setImageResource(R.mipmap.ic_bookmark_post_giangvien);
        }

        if (itemTimeLine.isSeen()) {
            itemPostHolder.getIvSeen().setVisibility(View.INVISIBLE);
        } else {
            itemPostHolder.getIvSeen().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return currentItemArr == null ? 0 : currentItemArr.size();
    }
}
