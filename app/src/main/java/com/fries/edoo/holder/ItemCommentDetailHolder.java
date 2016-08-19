package com.fries.edoo.holder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.R;
import com.fries.edoo.activities.PostDetailActivity;
import com.fries.edoo.activities.TimelineActivity;
import com.fries.edoo.adapter.PostDetailAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class ItemCommentDetailHolder extends AbstractHolder {

    private static final String TAG = "ItemCommentDetailHolder";
    private Context mContext;
    private ItemTimeLine itemTimeline;
    private ItemComment itemComment;

    private TextView tvAuthorName;
    private CircleImageView ivAuthorAvatar;
    private TextView tvComment;
    private CheckBox cbSolve;
    private PostDetailAdapter postDetailAdapter;

    public ItemCommentDetailHolder(View itemView) {
        super(itemView);

        this.mContext = itemView.getContext();

        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorname);
        ivAuthorAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        cbSolve = (CheckBox) itemView.findViewById(R.id.cb_vote);
    }

    public ItemCommentDetailHolder(View view, ItemTimeLine itemTimeline) {
        this(view);
        this.itemTimeline = itemTimeline;
    }

    public ItemCommentDetailHolder(View view, ItemTimeLine itemTimeline, PostDetailAdapter postDetailAdapter) {
        this(view, itemTimeline);
        this.postDetailAdapter = postDetailAdapter;
    }

    @Override
    public int getViewHolderType() {
        return 1;
    }

    public ItemComment getItemComment() {
        return itemComment;
    }

    public void setItemComment(final ItemComment itemComment) {
        this.itemComment = itemComment;

        tvAuthorName.setText(itemComment.getName());
        tvComment.setText(itemComment.getContent());
        if (!itemComment.getAvaUrl().isEmpty()) {
            Picasso.with(mContext)
                    .load(itemComment.getAvaUrl()).fit()
                    .placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user)
                    .into(ivAuthorAvatar);
        }

        cbSolve.setChecked(itemComment.isVote());
    }


    public void postSolve(final String idCmt) {
        Log.i(TAG, idCmt);

        String url = AppConfig.URL_VOTE_COMMENT;
        JSONObject params = new JSONObject();
        try {
            params.put("comment_id", idCmt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(mContext, Request.Method.POST, url, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error){
                    Log.d(TAG, response.toString());

                    itemTimeline.setSolve(true);
                    postDetailAdapter.setSolveCmt(idCmt);

                    Intent mIntent = new Intent();
                    Bundle b = new Bundle();
                    b.putSerializable("item_timeline", itemTimeline);
                    mIntent.putExtras(b);
                    PostDetailActivity postDetailActivity = (PostDetailActivity) mContext;
                    postDetailActivity.setResult(Activity.RESULT_OK, mIntent);
                }
                Log.d(TAG, message);
            }
        });

        requestServer.sendRequest("Post solve");
    }

    public CheckBox getCheckBoxVote() {
        return cbSolve;
    }
}
