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
import android.widget.Toast;

import com.fries.edoo.R;
import com.fries.edoo.holder.AbstractHolder;
import com.fries.edoo.holder.ItemPostHolder;
import com.fries.edoo.holder.ItemWritePostHolder;
import com.fries.edoo.models.ItemBase;
import com.fries.edoo.models.ItemTimeLine;
import com.fries.edoo.utils.CommonVLs;

import java.util.ArrayList;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<AbstractHolder> {
    private static final String TAG = "TimelineAdapter";
    private Context mContext;
    private ArrayList<ItemBase> itemArr;
    private String idLop;

    private boolean isLoadable;

    public static final int ITEM_TIMELINE = 0;
    public static final int ITEM_LOADMORE = 1;

    public TimeLineAdapter(Context context, String idLop) {
        this.mContext = context;
        this.idLop = idLop;
        this.isLoadable = true;
        itemArr = new ArrayList<>();
    }

    public void updateList(ArrayList<ItemBase> posts) {
        itemArr.clear();
        itemArr.addAll(posts);
        itemArr.add(null);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<ItemBase> posts) {
        itemArr.remove(itemArr.size()-1);
        itemArr.addAll(posts);
        itemArr.add(null);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemArr == null ? 0 : itemArr.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == itemArr.size() - 1) {
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
        if (position == itemArr.size() - 1) {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) abstractHolder;
            if (isLoadable) {
                loadMoreHolder.p.setVisibility(View.VISIBLE);
                loadMoreHolder.p.setIndeterminate(true);
                loadMoreHolder.tvDone.setVisibility(View.GONE);
            } else {
                loadMoreHolder.p.setVisibility(View.GONE);
                loadMoreHolder.tvDone.setVisibility(View.VISIBLE);
            }
        } else {
            ItemPostHolder itemPostHolder = (ItemPostHolder) abstractHolder;
            ItemTimeLine itemTimeLine = (ItemTimeLine) itemArr.get(position);
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

            setResourceTypePost(itemPostHolder, itemTimeLine.getType());
        }
    }

    private void setResourceTypePost(ItemPostHolder itemPostHolder, String type) {
        int idDrawable = android.R.color.white;
        if (type.equals(ItemTimeLine.TYPE_POST_NOTE)) idDrawable = R.drawable.ic_type_post_note;
        else if (type.equals(ItemTimeLine.TYPE_POST_QUESTION)) idDrawable = R.drawable.ic_type_post_question;
        else if (type.equals(ItemTimeLine.TYPE_POST_POLL)) idDrawable = R.drawable.ic_type_post_poll;
        else if (type.equals(ItemTimeLine.TYPE_POST_NOTIFICATION))
            idDrawable = R.drawable.ic_type_post_notification;
        itemPostHolder.getIvTypePost().setImageResource(idDrawable);
    }

    public void setLoadable(boolean loadable) {
        isLoadable = loadable;
    }

    public ArrayList<ItemBase> getItemArr() {
        return itemArr;
    }

    /**
     * Load more view holder
     */
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
