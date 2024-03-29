package com.uet.fries.edoo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.holder.AbstractHolder;
import com.uet.fries.edoo.holder.ItemCommentDetailHolder;
import com.uet.fries.edoo.holder.ItemEventDetailHolder;
import com.uet.fries.edoo.models.ItemComment;
import com.uet.fries.edoo.models.ItemTimeLineExercise;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class ExerciseDetailAdapter extends RecyclerView.Adapter<AbstractHolder> {

    private Context mContext;
    private ItemTimeLineExercise itemTimeline;
    private HashMap<String, String> user;
    private ItemEventDetailHolder eventDetail;

    public ExerciseDetailAdapter(Context mContext, ItemTimeLineExercise itemTimeLineExercise) {
        this.mContext = mContext;
        this.itemTimeline = itemTimeLineExercise;

        SQLiteHandler sqLite = new SQLiteHandler(mContext);
        user = sqLite.getUserDetails();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return AbstractHolder.TYPE_HEADER;
        } else {
            return AbstractHolder.TYPE_COMMENT;
        }
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AbstractHolder.TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_exercise_detail, parent, false);
            eventDetail = new ItemEventDetailHolder(view, itemTimeline, user.get("uid"), user.get("type"));
            return eventDetail;
        } else {
            View view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.item_comment_in_post, parent, false);
            return new ItemCommentDetailHolder(view, itemTimeline, this);
        }
    }

    @Override
    public void onBindViewHolder(AbstractHolder holder, int position) {
        if (position != 0) {
            final ItemCommentDetailHolder commentHolder = (ItemCommentDetailHolder) holder;
            final ItemComment itemComment = itemTimeline.getItemComments().get(position - 1);
            commentHolder.setItemComment(itemComment, user.get("uid"), user.get("type"));

            commentHolder.updateIvIsSolved();

        } else {
            ItemEventDetailHolder postDetailHolder = (ItemEventDetailHolder) holder;

            postDetailHolder.setTitle(itemTimeline.getTitle());
            postDetailHolder.setContentToWebview(itemTimeline.getContent());
            postDetailHolder.setAuthorName(itemTimeline.getNameAuthor());

            postDetailHolder.getTvCreateAt().setText(itemTimeline.getCreateAt());

            postDetailHolder.setDeadline(itemTimeline.getRemainingTime());
            postDetailHolder.setPercentSubmitted(itemTimeline.getPercentSubmitted());
            postDetailHolder.setIsSendFile(itemTimeline.getIsSendFile());
//            Log.i("set" + position, "percent = " + itemTimeline.getPercentSubmitted());
        }
    }


    @Override
    public int getItemCount() {
        if (itemTimeline == null) {
            return 0;
        } else {
            return itemTimeline.getItemComments().size() + 1;
        }
    }

    public void setItemTimeline(ItemTimeLineExercise itemTimeline) {
        this.itemTimeline = itemTimeline;
        notifyDataSetChanged();
    }

    public void setItemComments(ArrayList<ItemComment> commentArr) {
        this.itemTimeline.setItemComments(commentArr);
        notifyDataSetChanged();
    }

    public ItemEventDetailHolder getEventDetail(){
        return eventDetail;
    }
}
