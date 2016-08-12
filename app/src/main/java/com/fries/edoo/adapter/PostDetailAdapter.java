package com.fries.edoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import com.fries.edoo.PostDetailActivity;
import com.fries.edoo.R;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.holder.AbstractHolder;
import com.fries.edoo.holder.ItemCommentDetailHolder;
import com.fries.edoo.holder.ItemPostDetailHolder;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;

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
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_post_detail, parent, false);
            return new ItemPostDetailHolder(view, itemTimeline);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment_in_popup, parent, false);
            return new ItemCommentDetailHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(AbstractHolder holder, int position) {
        if (position != 0) {
            final ItemCommentDetailHolder commentHolder = (ItemCommentDetailHolder) holder;
            final ItemComment itemComment = itemTimeline.getItemComments().get(position - 1);
            commentHolder.setItemComment(itemComment);

            final CheckBox cbVote = commentHolder.getCheckBoxVote();

            commentHolder.setmHandler(mHandler);

            if (user.get("type").equalsIgnoreCase("student") || itemComment.isVote()) {
                cbVote.setClickable(false);
            } else {
                cbVote.setClickable(true);
//            cbVote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    postVoteByTeacher(itemComment.getIdComment(), user.get("uid"));
//                }
//            });

                cbVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentHolder.postVoteByTeacher(itemComment.getIdComment(), user.get("uid"));
//                    Log.i(TAG, "ok men");
                    }
                });
            }
        } else {
            ItemPostDetailHolder postDetailHolder = (ItemPostDetailHolder) holder;

            postDetailHolder.setTitle(itemTimeline.getTitle());
            postDetailHolder.setContent(itemTimeline.getContent());
            postDetailHolder.setAuthorName(itemTimeline.getName());
            postDetailHolder.setComment(itemTimeline.getItemComments().size() + "");
            postDetailHolder.setLike(itemTimeline.getLike() + "");
            if (itemTimeline.getLike() >= 0) {
                postDetailHolder.getIvLike().setImageResource(R.mipmap.ic_up_24);
            } else {
                postDetailHolder.getIvLike().setImageResource(R.mipmap.ic_down_24);
            }

            postDetailHolder.getTvCreateAt().setText(", " + itemTimeline.getCreateAt());

            postDetailHolder.setCbIsVote(itemTimeline.isConfirmByTeacher());
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            cbVote.setClickable(false);
            itemTimeline.setIsConfirmByTeacher(true);
            notifyItemChanged(0);

            PostDetailActivity postDetailActivity = (PostDetailActivity) mContext;
            postDetailActivity.setResult(Activity.RESULT_OK);
        }
    };

    @Override
    public int getItemCount() {
        return itemTimeline.getItemComments().size() + 1;
    }

    public void setItemTimeline(ItemTimeLine itemTimeline) {
        this.itemTimeline = itemTimeline;
        notifyDataSetChanged();
    }

    public void setItemComments(ArrayList<ItemComment> commentArr){
        this.itemTimeline.setItemComments(commentArr);
        notifyDataSetChanged();
    }
}
