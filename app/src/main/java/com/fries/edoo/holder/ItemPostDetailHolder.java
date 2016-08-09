package com.fries.edoo.holder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.PostDetailActivity;
import com.fries.edoo.R;
import com.fries.edoo.adapter.ImagePostDetailAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.helper.SQLiteHandler;
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
public class ItemPostDetailHolder extends AbstractHolder {

    private static final String TAG = "ItemPostDetailHolder";
    private static final int POST_COMMENT_SUCCESS = 12;
    private static final int POST_LIKE_SUCCESS = 11;
    private static final int POST_DISLIKE_SUCCESS = 10;
    private static final int POST_VOTE_SUCCESS = 13;
    private ItemTimeLine itemTimeLine;
    private Context mContext;
    private ProgressDialog pDialog;

    private ListView lvImgPost;
    private ImagePostDetailAdapter mAdapter;

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvAuthorName;
    private TextView tvLike;
    private TextView tvComment;
    private ImageView btnLike;
    private ImageView btnDisLike;
    private CircleImageView ivAvatar;
    private ImageView ivLike;
    private TextView tvCreateAt;
    //    private CheckBox cbIsVote;
    private ImageView ivIsVote;

    public ItemPostDetailHolder(View itemView) {
        super(itemView);
    }

    public ItemPostDetailHolder(View itemView, ItemTimeLine itemTimeLine) {
        this(itemView);
        this.itemTimeLine = itemTimeLine;
        this.mContext = itemView.getContext();

        pDialog = new ProgressDialog(mContext);

        lvImgPost = (ListView) itemView.findViewById(R.id.lv_imagePost);
        mAdapter = new ImagePostDetailAdapter(itemView.getContext());
        lvImgPost.setAdapter(mAdapter);

        //test
        lvImgPost.setVisibility(View.GONE);

        ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_nameauthor);
        tvLike = (TextView) itemView.findViewById(R.id.tv_like);
        tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        btnLike = (ImageView) itemView.findViewById(R.id.btn_like);
        btnDisLike = (ImageView) itemView.findViewById(R.id.btn_dislike);

        tvCreateAt = (TextView) itemView.findViewById(R.id.tv_time_post);
//        cbIsVote = (CheckBox) itemView.findViewById(R.id.cb_isVote);
        ivIsVote = (ImageView) itemView.findViewById(R.id.iv_bookmark);

        Picasso.with(mContext).load(itemTimeLine.getAva()).placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user)
                .into(ivAvatar);
        createListener();
    }

    public void setCbIsVote(boolean isConfirmByTeacher) {
//        cbIsVote.setChecked(isConfirmByTeacher);
//        cbIsVote.setClickable(false);

        boolean isPostByTeacher = itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher");

        if (isPostByTeacher) {
            ivIsVote.setVisibility(View.VISIBLE);
            ivIsVote.setImageResource(R.mipmap.ic_bookmark_post_giangvien);
        } else if (isConfirmByTeacher) {
            ivIsVote.setVisibility(View.VISIBLE);
            ivIsVote.setImageResource(R.mipmap.ic_bookmark_check);
        } else {
            ivIsVote.setVisibility(View.INVISIBLE);
        }
    }

    private void createListener() {
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get user
                SQLiteHandler db = new SQLiteHandler(mContext);
                HashMap<String, String> user = db.getUserDetails();
                final String uid = user.get("uid");

                postLike(uid, itemTimeLine.getIdPost());
            }
        });

        btnDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get user
                SQLiteHandler db = new SQLiteHandler(mContext);
                HashMap<String, String> user = db.getUserDetails();
                final String uid = user.get("uid");

                postDisLike(uid, itemTimeLine.getIdPost());
            }
        });
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setContent(String content) {
        tvContent.setText(content);
    }

    public void setAuthorName(String authorName) {
        tvAuthorName.setText(authorName);
    }

    public void setLike(String like) {
        tvLike.setText(like + "");
    }

    public void setComment(String comment) {
        tvComment.setText(comment + "");
    }

    public ImageView getIvLike() {
        return ivLike;
    }

    public TextView getTvCreateAt() {
        return tvCreateAt;
    }

    public ImageView getBtnLike() {
        return btnLike;
    }


    @Override
    public int getViewHolderType() {
        return 0;
    }


    //Post like and cmt to server
    private void postLike(final String uid, final String idPost) {
        showDialog();
        Log.i(TAG, uid);
        Log.i(TAG, idPost);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_POST_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();

                        Log.i(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean error = jsonObject.getBoolean("error");
                            if (!error) {
                                // cap nhat giao dien
                                // thong bao dang bai thanh cong
//                                JSONObject jsonComment = jsonObject.getJSONObject("comment");
//                                String idCmt = jsonComment.getString("id");

//                                Bundle b = new Bundle();
//                                b.putString("idCmt", idCmt);
//                                b.putString("content", content);

                                Message msg = new Message();
//                                msg.setData(b);
                                msg.arg1 = POST_LIKE_SUCCESS;
                                msg.setTarget(mHandler);
                                msg.sendToTarget();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.i(TAG, "Vote error: " + error.getMessage());
                Toast.makeText(mContext, error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("user", uid);
                data.put("id", idPost);

                return data;
            }
        };

        AppController.getInstance().addToRequestQueue(request, "postlike");
    }


    //Post dislike and cmt to server
    private void postDisLike(final String uid, final String idPost) {
        showDialog();
        Log.i(TAG, uid);
        Log.i(TAG, idPost);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_POST_DISLIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();

                        Log.i(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean error = jsonObject.getBoolean("error");
                            if (!error) {
                                // cap nhat giao dien
                                // thong bao dang bai thanh cong
//                                JSONObject jsonComment = jsonObject.getJSONObject("comment");
//                                String idCmt = jsonComment.getString("id");

//                                Bundle b = new Bundle();
//                                b.putString("idCmt", idCmt);
//                                b.putString("content", content);

                                Message msg = new Message();
//                                msg.setData(b);
                                msg.arg1 = POST_DISLIKE_SUCCESS;
                                msg.setTarget(mHandler);
                                msg.sendToTarget();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.i(TAG, "Vote error: " + error.getMessage());
                Toast.makeText(mContext, error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("user", uid);
                data.put("id", idPost);

                return data;
            }
        };

        AppController.getInstance().addToRequestQueue(request, "postdislike");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case POST_LIKE_SUCCESS:
                    itemTimeLine.setLike(itemTimeLine.getLike() + 1);
                    break;
                case POST_DISLIKE_SUCCESS:
                    itemTimeLine.setLike(itemTimeLine.getLike() - 1);
                    break;
            }
            tvLike.setText(itemTimeLine.getLike() + "");
            if (itemTimeLine.getLike() >= 0) {
                ivLike.setImageResource(R.mipmap.ic_up_24);
            } else {
                ivLike.setImageResource(R.mipmap.ic_down_24);
            }

            PostDetailActivity postDetailActivity = (PostDetailActivity) mContext;
            postDetailActivity.setResult(Activity.RESULT_OK);
//            switch (msg.arg1){
//                case POST_LIKE_SUCCESS:
//
//                    break;
//                case POST_VOTE_SUCCESS:
//
//                    break;
//            }
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

}
