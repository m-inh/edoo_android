package com.uet.fries.edoo.holder;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.uet.fries.edoo.activities.TimelineActivity;
import com.uet.fries.edoo.models.ItemComment;
import com.uet.fries.edoo.models.ItemTimeLine;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ItemPostHolder extends AbstractHolder {
    private static final String TAG = "ItemPostHolder";
    private ArrayList<ItemComment> listComment = new ArrayList<>();
    private Context mContext;
    private ImageView ivBookmark;
    private ImageView ivLike;
    private CircleImageView ivSeen;
    private ImageView ivTypePost;

    private int like;

    private String idLop;
    private String keyLopType;
    private String idPost;
    private ItemTimeLine itemTimeLine;

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
        return 1;
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

//                postSeen(itemTimeLine.getIdPost());
            }
        });
    }

//    private void postSeen(String idPost) {
//        String url = AppConfig.URL_POST_SEEN;
//        JSONObject params = new JSONObject();
//        try {
//            params.put("post_id", idPost);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestServer requestServer = new RequestServer(mContext, Request.Method.POST, url, params);
//        requestServer.sendRequest("Post a seen");
//    }

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

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public TextView getTxtContent() {
        return txtContent;
    }

    public TextView getTxtCountLike() {
        return txtCountLike;
    }

    public TextView getTxtCountComment() {
        return txtCountComment;
    }

    public TextView getTxtAuthor() {
        return txtAuthor;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public void setItemTimeLine(ItemTimeLine itemTimeLine) {
        this.itemTimeLine = itemTimeLine;
    }

    public void setIdLop(String idLop) {
        this.idLop = idLop;
    }

    public void setKeyLopType(String keyLopType) {
        this.keyLopType = keyLopType;
    }

    public ImageView getIvBookmark() {
        return ivBookmark;
    }

    public ImageView getIvLike() {
        return ivLike;
    }

    public TextView getTvTimeCreateAt() {
        return tvTimeCreateAt;
    }

    public CircleImageView getIvSeen() {
        return ivSeen;
    }

    public ImageView getIvTypePost(){
        return ivTypePost;
    }
}
