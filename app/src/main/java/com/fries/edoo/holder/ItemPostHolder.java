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

//import com.hackathon.fries.myclass.dialog.PopupComments;

/**
 * Created by Tdh4vn on 11/21/2015.
 */
public class ItemPostHolder extends AbstractHolder {
    private static final String TAG = "ItemPostHolder";
    private ArrayList<ItemComment> listComment = new ArrayList<>();
    private Context mContext;
    //    private CheckBox checkBox;
    private ImageView ivBookmark;
    private ImageView ivLike;
    private CircleImageView ivSeen;
    private ProgressDialog pDialog;

    private int like;

    private String idLop;
    private String keyLopType;
    private String idPost;
    private ItemTimeLine itemTimeLine;

    private OnClickItemPost onClickItemPost;

    private View rootView;

    public ItemPostHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        mContext = itemView.getContext();

        pDialog = new ProgressDialog(mContext);
        imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
        ivSeen = (CircleImageView) itemView.findViewById(R.id.iv_marker_seen);

        txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        txtContent = (TextView) itemView.findViewById(R.id.tv_content);
        txtAuthor = (TextView) itemView.findViewById(R.id.tv_name_postauthor);
        ivBookmark = (ImageView) itemView.findViewById(R.id.iv_bookmark);
        ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
        tvTimeCreateAt = (TextView) itemView.findViewById(R.id.tv_time_post);
//        imgAvatarLastPost = (ImageView) itemView.findViewById(R.id.imgAvaComment);
//        txtNameLastPost = (TextView) itemView.findViewById(R.id.txtUserName);
//
//        txtCommentLastPost = (TextView) itemView.findViewById(R.id.txtContentComment);

        txtCountLike = (TextView) itemView.findViewById(R.id.txtCountLike);
        txtCountComment = (TextView) itemView.findViewById(R.id.txtCountComment);
//        btnTks = (Button) itemView.findViewById(R.id.btnTks);
//        btnComment = (Button) itemView.findViewById(R.id.btnComment);

//        checkBox = (CheckBox) itemView.findViewById(R.id.cb_vote);
//        checkBox.setClickable(false);
//        startAnim();
        createListener(itemView);
    }

    public ItemPostHolder(View itemView, OnClickItemPost onClickItemPost) {
        this(itemView);
        this.onClickItemPost = onClickItemPost;
    }

    public void startAnim() {
        Animation myAni = AnimationUtils.loadAnimation(mContext, R.anim.anim_show_item_listview);
        itemView.startAnimation(myAni);
    }

    public void setListComment(ArrayList<ItemComment> arr) {
        listComment = arr;
    }

    public void setAva(ItemTimeLine itemTimeLine) {
        Picasso.with(mContext)
                .load(itemTimeLine.getAva()).fit()
                .placeholder(R.mipmap.ic_user)
                .placeholder(R.mipmap.ic_user).into(imgAvatar);
    }

    private void createListener(View itemView) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "Positino: " + getAdapterPosition());
                itemTimeLine.setIsSeen(true);
                onClickItemPost.onClick(getAdapterPosition());
                TimelineActivity timelineActivity = (TimelineActivity) mContext;
                timelineActivity.startPostDetailActivity(itemTimeLine);

                postSeen(itemTimeLine.getIdPost());
            }
        });
    }

    private void postSeen(String idPost) {
        String url = AppConfig.URL_POST_SEEN;
        JSONObject params = new JSONObject();
        try {
            params.put("post_id", idPost);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(mContext, Request.Method.POST, url, params);
        requestServer.sendRequest("Post a seen");
    }

    private LinearLayout layoutParent;
    private CircleImageView imgAvatar;
    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtAuthor;
    private TextView tvTimeCreateAt;
    //    private Button btnLike;
    //    private ImageView imgAvatarLastPost;
//    private TextView txtNameLastPost;
//    private TextView txtCommentLastPost;
    private TextView txtCountLike;
    private TextView txtCountComment;
//    private Button btnTks;
//    private Button btnComment;

    @Override
    public int getViewHolderType() {
        int viewHolderType = 1;
        return viewHolderType;
    }

    public ImageView getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(CircleImageView imgAvatar) {
        this.imgAvatar = imgAvatar;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(TextView txtTitle) {
        this.txtTitle = txtTitle;
    }

    public TextView getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(TextView txtContent) {
        this.txtContent = txtContent;
    }

    public TextView getTxtCountLike() {
        return txtCountLike;
    }

    public void setTxtCountLike(TextView txtCountLike) {
        this.txtCountLike = txtCountLike;
    }

    public TextView getTxtCountComment() {
        return txtCountComment;
    }

    public void setTxtCountComment(TextView txtCountComment) {
        this.txtCountComment = txtCountComment;
    }

    public interface OnClickItemPost {
        void onClick(int position);
    }

    public TextView getTxtAuthor() {
        return txtAuthor;
    }

    public void setTxtAuthor(TextView txtAuthor) {
        this.txtAuthor = txtAuthor;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            like ++;
            txtCountLike.setText(like + "");
            if (like >= 0) {
                ivLike.setImageResource(R.mipmap.ic_up_24);
            } else {
                ivLike.setImageResource(R.mipmap.ic_down_24);
            }
        }
    };

    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.hide();
        }
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public ItemTimeLine getItemTimeLine() {
        return itemTimeLine;
    }

    public void setItemTimeLine(ItemTimeLine itemTimeLine) {
        this.itemTimeLine = itemTimeLine;
        setAva(itemTimeLine);
    }

    public String getIdLop() {
        return idLop;
    }

    public void setIdLop(String idLop) {
        this.idLop = idLop;
    }

    public String getKeyLopType() {
        return keyLopType;
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

    public void setIvSeen(CircleImageView ivSeen) {
        this.ivSeen = ivSeen;
    }
}
