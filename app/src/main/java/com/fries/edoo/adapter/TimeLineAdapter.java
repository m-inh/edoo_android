package com.fries.edoo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private boolean isLoadable;

    public static final int ITEM_TIMELINE = 0;
    public static final int ITEM_LOADMORE = 1;

    public static final int BAI_DANG_BINH_THUONG = 1;
    public static final int BAI_DANG_LOC_THEO_GIAO_VIEN = 2;
    public static final int BAI_DANG_LOC_THEO_CHUA_TRA_LOI = 3;
    public static final int BAI_DANG_CHUA_DOC = 4;

    public int currentMode = BAI_DANG_BINH_THUONG;

    public TimeLineAdapter(Context context, String idLop) {
        this.mContext = context;
        this.idLop = idLop;
        this.isLoadable = true;
        itemArr = new ArrayList<>();
        currentItemArr = new ArrayList<>();
    }

    public void updateList(ArrayList<ItemBase> posts) {
        itemArr.clear();
        itemArr.addAll(posts);
//        currentItemArr.clear();
//        currentItemArr.add(null);
//        this.currentItemArr.addAll(posts);
        locBaiDang(currentMode);
    }

    public void addItems(ArrayList<ItemBase> posts) {
//        itemArr.remove(itemArr.size()-1);
        itemArr.addAll(posts);
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
        currentItemArr.add(null);
        currentMode = mode;
        notifyDataSetChanged();
    }

    public void refreshList() {
        locBaiDang(currentMode);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == currentItemArr.size() - 1) {
            return ITEM_LOADMORE;
        } else {
            return ITEM_TIMELINE;
        }
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TIMELINE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
            return new ItemPostHolder(view);
        } else if (viewType == ITEM_LOADMORE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_loadmore, parent, false);
            return new LoadMoreHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AbstractHolder abstractHolder, int position) {
        if (position == currentItemArr.size() - 1) {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) abstractHolder;
            if (isLoadable){
                loadMoreHolder.p.setVisibility(View.VISIBLE);
                loadMoreHolder.p.setIndeterminate(true);
                loadMoreHolder.tvDone.setVisibility(View.GONE);
            } else {
                loadMoreHolder.p.setVisibility(View.GONE);
                loadMoreHolder.tvDone.setVisibility(View.VISIBLE);
            }
        } else {
            ItemPostHolder itemPostHolder = (ItemPostHolder) abstractHolder;
            ItemTimeLine itemTimeLine = (ItemTimeLine) currentItemArr.get(position);
//            if (itemTimeLine == null){
//                return;
//            }
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
            if (countCmt == 0) {
                countCmt = itemTimeLine.getCommentCount();
            }
            itemPostHolder.getTxtCountComment().setText(countCmt + "");
            itemPostHolder.getTvTimeCreateAt().setText(", " + itemTimeLine.getCreateAt());

            itemPostHolder.getIvBookmark().setVisibility(View.INVISIBLE);

            boolean isPostByTeacher = itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher");

            if (itemTimeLine.isSolve()) {
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
    }

    @Override
    public int getItemCount() {
        return currentItemArr == null ? 0 : currentItemArr.size();
    }

    public boolean isLoadable() {
        return isLoadable;
    }

    public void setLoadable(boolean loadable) {
        isLoadable = loadable;
    }

    public class LoadMoreHolder extends AbstractHolder {
        public ProgressBar p;
        public TextView tvDone;

        public LoadMoreHolder(View itemView) {
            super(itemView);

            p = (ProgressBar) itemView.findViewById(R.id.pb_load);
            tvDone = (TextView) itemView.findViewById(R.id.tv_done);
        }

        @Override
        public int getViewHolderType() {
            return ITEM_LOADMORE;
        }
    }


}
