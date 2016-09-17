package com.uet.fries.edoo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.holder.AbstractHolder;
import com.uet.fries.edoo.holder.ItemCommentDetailHolder;
import com.uet.fries.edoo.holder.ItemPostDetailHolder;
import com.uet.fries.edoo.models.ItemComment;
import com.uet.fries.edoo.models.ItemTimeLine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class PostDetailAdapter extends RecyclerView.Adapter<AbstractHolder> {

    private Context mContext;
    private ItemTimeLine itemTimeline;
    private HashMap<String, String> user;

    public PostDetailAdapter(Context mContext, ItemTimeLine itemTimeline) {
        this.mContext = mContext;
        this.itemTimeline = itemTimeline;

        SQLiteHandler sqLite = new SQLiteHandler(mContext);
        user = sqLite.getUserDetails();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.item_post_detail, parent, false);
            return new ItemPostDetailHolder(view, itemTimeline);
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
            ItemPostDetailHolder postDetailHolder = (ItemPostDetailHolder) holder;

            postDetailHolder.setTitle(itemTimeline.getTitle());
            postDetailHolder.setContentToWebview(itemTimeline.getContent());
            postDetailHolder.setAuthorName(itemTimeline.getName());
            postDetailHolder.setComment(itemTimeline.getItemComments().size() + "");
            postDetailHolder.setLike(itemTimeline.getLike() + "");
            if (itemTimeline.getLike() >= 0) {
                postDetailHolder.getIvLike().setImageResource(com.uet.fries.edoo.R.drawable.ic_vote_up);
            } else {
                postDetailHolder.getIvLike().setImageResource(com.uet.fries.edoo.R.drawable.ic_vote_down);
            }

            postDetailHolder.getTvCreateAt().setText(itemTimeline.getCreateAt());

            postDetailHolder.setCbIsVote();

            setResourceTypePost(postDetailHolder, itemTimeline.getType());
        }
    }

    private void setResourceTypePost(ItemPostDetailHolder postDetailHolder, String type) {
        int idDrawable = android.R.color.white;
        if (type.equals(ItemTimeLine.TYPE_POST_NOTE)) idDrawable = com.uet.fries.edoo.R.drawable.ic_type_post_note;
        else if (type.equals(ItemTimeLine.TYPE_POST_QUESTION))
            idDrawable = com.uet.fries.edoo.R.drawable.ic_type_post_question;
        else if (type.equals(ItemTimeLine.TYPE_POST_POLL))
            idDrawable = com.uet.fries.edoo.R.drawable.ic_type_post_poll;
        else if (type.equals(ItemTimeLine.TYPE_POST_NOTIFICATION))
            idDrawable = com.uet.fries.edoo.R.drawable.ic_type_post_notification;
        postDetailHolder.getIvTypePost().setImageResource(idDrawable);
    }

    @Override
    public int getItemCount() {
        if (itemTimeline == null){
            return 0;
        } else {
            return itemTimeline.getItemComments().size() + 1;
        }
    }

    public void setItemTimeline(ItemTimeLine itemTimeline) {
        this.itemTimeline = itemTimeline;
        notifyDataSetChanged();
    }

    public void setItemComments(ArrayList<ItemComment> commentArr) {
        this.itemTimeline.setItemComments(commentArr);
        notifyDataSetChanged();
    }

    public void setSolveCmt(String cmtId) {
        ArrayList<ItemComment> cmts = itemTimeline.getItemComments();
        for (int i = 0; i < cmts.size(); i++) {
            if (cmts.get(i).getIdComment().equalsIgnoreCase(cmtId)) {
                cmts.get(i).setIsSolved(true);
            } else {
                cmts.get(i).setIsSolved(false);
            }
        }
        notifyDataSetChanged();
    }

    public void setUnsolveCmt() {
        ArrayList<ItemComment> cmts = itemTimeline.getItemComments();
        for (int i = 0; i < cmts.size(); i++) {
            cmts.get(i).setIsSolved(false);
        }
        notifyDataSetChanged();
    }
}
