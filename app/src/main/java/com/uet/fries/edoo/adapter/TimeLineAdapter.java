package com.uet.fries.edoo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.holder.AbstractHolder;
import com.uet.fries.edoo.holder.ItemPostHolder;
import com.uet.fries.edoo.holder.ItemTimelineExerciseHolder;
import com.uet.fries.edoo.models.ITimelineBase;
import com.uet.fries.edoo.models.ItemTimeLineExercise;
import com.uet.fries.edoo.models.ItemTimeLinePost;

import java.util.ArrayList;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<AbstractHolder> {
    private static final String TAG = TimeLineAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<ITimelineBase> itemArr;
    private String idLop;

    private boolean isLoadable;

    public static final int ITEM_TIMELINE = 0;
    public static final int ITEM_LOADMORE = 1;
    public static final int ITEM_EXERCISE = 2;

    public TimeLineAdapter(Context context, String idLop) {
        this.mContext = context;
        this.idLop = idLop;
        this.isLoadable = true;
        itemArr = new ArrayList<>();
    }

    public void updateList(ArrayList<ITimelineBase> posts) {
        itemArr.clear();
        itemArr.addAll(posts);
        itemArr.add(null);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<ITimelineBase> posts) {
        itemArr.remove(itemArr.size() - 1);
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
            ITimelineBase itemTimeLine = itemArr.get(position);
            if (itemTimeLine.getType().equalsIgnoreCase(ItemTimeLinePost.TYPE_POST_EXERCISE)) {
                return ITEM_EXERCISE;
            } else return ITEM_TIMELINE;
        }
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AbstractHolder holder = null;
        View view = null;
        if (viewType == ITEM_TIMELINE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_timeline, parent, false);
            holder = new ItemPostHolder(view);
        } else if (viewType == ITEM_LOADMORE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_loadmore, parent, false);
            holder = new LoadMoreHolder(view);
        } else if (viewType == ITEM_EXERCISE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_timeline_exercise, parent, false);
            holder = new ItemTimelineExerciseHolder(view);
        }
//        Animation myAni = AnimationUtils.loadAnimation(mContext, R.anim.anim_show_itemtimeline_fadein);
//        if (view != null) {
//            view.startAnimation(myAni);
//        }
        return holder;
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
                loadMoreHolder.tvDone.setVisibility(View.GONE);

                if (position == 0) {
                    loadMoreHolder.tvDone.setText("Lớp học chưa có bài đăng nào");
                    loadMoreHolder.tvDone.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (abstractHolder.getViewHolderType() == ITEM_TIMELINE) {
                ItemTimeLinePost itemTimeLine = (ItemTimeLinePost) itemArr.get(position);
                ItemPostHolder itemPostHolder = (ItemPostHolder) abstractHolder;
                itemPostHolder.setItemTimeLine(itemTimeLine);
                itemPostHolder.setIdLop(idLop);

            } else if (abstractHolder.getViewHolderType() == ITEM_EXERCISE) {
                ItemTimeLineExercise itemTimeLine = (ItemTimeLineExercise) itemArr.get(position);
                ItemTimelineExerciseHolder itemPostHolder = (ItemTimelineExerciseHolder) abstractHolder;
                itemPostHolder.setItemTimeLineExercise(itemTimeLine);
            }
        }
    }

    public void setLoadable(boolean loadable) {
        isLoadable = loadable;
    }

    public ArrayList<ITimelineBase> getItemArr() {
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

            p = (ProgressBar) itemView.findViewById(com.uet.fries.edoo.R.id.pb_load);
            tvDone = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_done);
        }

        @Override
        public int getViewHolderType() {
            return ITEM_LOADMORE;
        }
    }


}
