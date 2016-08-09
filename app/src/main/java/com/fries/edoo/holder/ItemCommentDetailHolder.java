package com.fries.edoo.holder;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemComment;
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
    //    private ItemTimeLine itemTimeline;
    private Context mContext;
    private ItemComment itemComment;
    private ProgressDialog pDialog;

    private TextView tvAuthorName;
    private CircleImageView ivAuthorAvatar;
    private TextView tvComment;
    private CheckBox cbVote;


    public ItemCommentDetailHolder(View itemView) {
        super(itemView);

        this.mContext = itemView.getContext();

        pDialog = new ProgressDialog(mContext);

        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorname);
        ivAuthorAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        cbVote = (CheckBox) itemView.findViewById(R.id.cb_vote);
    }

//    public ItemCommentDetailHolder(View itemView, ItemComment itemComment){
//        this(itemView);
//        this.itemComment = itemComment;
//    }

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
        Log.i(TAG, "url: " + itemComment.getAvaUrl());
        if (!itemComment.getAvaUrl().isEmpty()) {
            Picasso.with(mContext)
                    .load(itemComment.getAvaUrl()).
                    placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user)
                    .into(ivAuthorAvatar);
        }

        cbVote.setChecked(itemComment.isVote());

        SQLiteHandler sqLite = new SQLiteHandler(mContext);
        final HashMap<String, String> user = sqLite.getUserDetails();
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
                    postVoteByTeacher(itemComment.getIdComment(), user.get("uid"));
//                    Log.i(TAG, "ok men");
                }
            });
        }

    }


    public void postVoteByTeacher(final String idCmt, final String uid) {
        showDialog();
        Log.i(TAG, idCmt);
        Log.i(TAG, uid);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_VOTE_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cbVote.setClickable(false);
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
//                                msg.arg1 = POST_VOTE_SUCCESS;
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
                data.put("id", idCmt);

                return data;
            }
        };

        AppController.getInstance().addToRequestQueue(request, "post vote by teacher");
    }

    private Handler mHandler;
//            = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Log.i(TAG, "ok");
//            cbVote.setClickable(false);
//        }
//    };

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

    public CheckBox getCheckBoxVote() {
        return cbVote;
    }


    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
