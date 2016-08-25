package com.fries.edoo.holder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.R;
import com.fries.edoo.activities.TimelineActivity;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        ivSeen = (CircleImageView) itemView.findViewById(R.id.iv_marker_seen);
        ivTypePost = (ImageView) itemView.findViewById(R.id.iv_type_post_list);

        txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        txtContent = (TextView) itemView.findViewById(R.id.tv_content);
        txtAuthor = (TextView) itemView.findViewById(R.id.tv_name_postauthor);
        ivBookmark = (ImageView) itemView.findViewById(R.id.iv_bookmark);
        ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
        tvTimeCreateAt = (TextView) itemView.findViewById(R.id.tv_time_post);
        txtCountLike = (TextView) itemView.findViewById(R.id.txtCountLike);
        txtCountComment = (TextView) itemView.findViewById(R.id.txtCountComment);

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
        Animation myAni = AnimationUtils.loadAnimation(mContext, R.anim.anim_show_item_listview);
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
