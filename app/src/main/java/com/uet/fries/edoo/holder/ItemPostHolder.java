package com.uet.fries.edoo.holder;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.TimelineActivity;
import com.uet.fries.edoo.adapter.TimeLineAdapter;
import com.uet.fries.edoo.models.ITimelineBase;
import com.uet.fries.edoo.models.ItemComment;
import com.uet.fries.edoo.models.ItemTimeLinePost;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ItemPostHolder extends AbstractHolder {
    private static final String TAG = ItemPostHolder.class.getSimpleName();
    private ArrayList<ItemComment> listComment = new ArrayList<>();
    private Context mContext;
    private ImageView ivBookmark;
    private ImageView ivLike;
    private CircleImageView ivSeen;
    private ImageView ivTypePost;

    private String idLop;
    private ItemTimeLinePost itemTimeLine;

    private View rootView;

    public ItemPostHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        mContext = itemView.getContext();

        ivSeen = (CircleImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_marker_seen);
        ivTypePost = (ImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_type_post_list);

        txtTitle = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.txtTitle);
        txtContent = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_content);
        txtAuthor = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_name_postauthor);
        ivBookmark = (ImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_bookmark);
        ivLike = (ImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_like);
        tvTimeCreateAt = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_time_post);
        txtCountLike = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.txtCountLike);
        txtCountComment = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.txtCountComment);

//        startAnim();
        createListener(itemView);
    }

    @Override
    public int getViewHolderType() {
        return TimeLineAdapter.ITEM_TIMELINE;
    }

    public void setListComment(ArrayList<ItemComment> arr) {
        listComment = arr;
    }

    private void createListener(View itemView) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemTimeLine.setIsSeen(true);
                TimelineActivity timelineActivity = (TimelineActivity) mContext;
                timelineActivity.startPostDetailActivity(itemTimeLine);
            }
        });
    }

    public void startAnim() {
        Animation myAni = AnimationUtils.loadAnimation(mContext, com.uet.fries.edoo.R.anim.anim_show_item_listview);
        itemView.startAnimation(myAni);
    }

    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtAuthor;
    private TextView tvTimeCreateAt;
    private TextView txtCountLike;
    private TextView txtCountComment;

    public void setLike(int like) {
        txtCountLike.setText(itemTimeLine.getLike() + "");
        if (itemTimeLine.getLike() >= 0) {
            ivLike.setImageResource(com.uet.fries.edoo.R.drawable.ic_vote_up);
        } else {
            ivLike.setImageResource(com.uet.fries.edoo.R.drawable.ic_vote_down);
        }
    }

    public void setAuthor(String author) {
        this.txtAuthor.setText(author);
    }

    public void setTitle(String title) {
        this.txtTitle.setText(title);
    }

    public void setContent(String content) {
        this.txtContent.setText(content);
    }

    public void setCountComment(String count) {
        this.txtCountComment.setText(count);
    }

    public void setIdLop(String idLop) {
        this.idLop = idLop;
    }

    public void setTimeCreateAt(String time) {
        this.tvTimeCreateAt.setText(time);
    }

    public void setItemTimeLine(ItemTimeLinePost itemTimeLine) {
        this.itemTimeLine = itemTimeLine;

        setDataFromItemTimeline();
    }

    private void setDataFromItemTimeline() {
        setListComment(itemTimeLine.getItemComments());

        setTitle(itemTimeLine.getTitle());
        setContent(itemTimeLine.getSummary());
        setLike(itemTimeLine.getLike());
        setAuthor(itemTimeLine.getNameAuthor());
        setTimeCreateAt(", " + itemTimeLine.getCreateAt());


        int countCmt = itemTimeLine.getItemComments().size();
        if (countCmt == 0) {
            countCmt = itemTimeLine.getCommentCount();
        }
        setCountComment(countCmt + "");


        ivBookmark.setVisibility(View.GONE);

        boolean isPostByTeacher = itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher");

        if (itemTimeLine.isSolve()) {
            ivBookmark.setVisibility(View.VISIBLE);
            ivBookmark.setImageResource(com.uet.fries.edoo.R.drawable.ic_bookmark_solved);
        }
        if (isPostByTeacher) {
            ivBookmark.setVisibility(View.VISIBLE);
            ivBookmark.setImageResource(com.uet.fries.edoo.R.drawable.ic_bookmark_post_teacher);
        }

        if (itemTimeLine.isSeen()) {
            ivSeen.setVisibility(View.INVISIBLE);
        } else {
            ivSeen.setVisibility(View.VISIBLE);
        }

        setResourceTypePost();
    }

    private void setResourceTypePost() {
        int idDrawable = android.R.color.white;
        switch (itemTimeLine.getType()) {
            case ITimelineBase.TYPE_POST_NOTE:
                idDrawable = R.drawable.ic_type_post_note;
                break;
            case ITimelineBase.TYPE_POST_QUESTION:
                idDrawable = R.drawable.ic_type_post_question;
                break;
            case ITimelineBase.TYPE_POST_POLL:
                idDrawable = R.drawable.ic_type_post_poll;
                break;
            case ITimelineBase.TYPE_POST_NOTIFICATION:
                idDrawable = R.drawable.ic_type_post_notification;
                break;
        }
        ivTypePost.setImageResource(idDrawable);
    }
}
