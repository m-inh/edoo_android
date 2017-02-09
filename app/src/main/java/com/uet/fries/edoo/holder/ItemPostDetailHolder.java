package com.uet.fries.edoo.holder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.uet.fries.edoo.activities.PostDetailActivity;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.WebviewActivity;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.squareup.picasso.Picasso;
import com.uet.fries.edoo.models.ItemTimeLinePost;
import com.uet.fries.edoo.utils.PermissionManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.webkit.WebView.HitTestResult.IMAGE_TYPE;
import static android.webkit.WebView.HitTestResult.SRC_ANCHOR_TYPE;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class ItemPostDetailHolder extends AbstractHolder {

    private static final String TAG = ItemPostDetailHolder.class.getSimpleName();
    private ItemTimeLinePost itemTimeLine;
    private String userId;
    private Context mContext;

    private TextView tvTitle;
    private TextView tvContent;
    private WebView webView;
    private TextView tvAuthorName;
    private TextView tvLike;
    private TextView tvComment;
    private ImageView btnLike;
    private ImageView btnDisLike;
    private CircleImageView ivAvatar;
    private ImageView ivLike;
    private TextView tvCreateAt;
    private ImageView ivIsVote;
    private ImageView ivTypePost;

    public ItemPostDetailHolder(View itemView) {
        super(itemView);
    }

    public ItemPostDetailHolder(View itemView, ItemTimeLinePost itemTimeLine, String userId) {
        this(itemView);
        this.itemTimeLine = itemTimeLine;
        this.userId = userId;
        this.mContext = itemView.getContext();

        ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_nameauthor);
        tvLike = (TextView) itemView.findViewById(R.id.tv_like);
        tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        btnLike = (ImageView) itemView.findViewById(R.id.btn_like);
        btnDisLike = (ImageView) itemView.findViewById(R.id.btn_dislike);
        ivTypePost = (ImageView) itemView.findViewById(R.id.iv_type_post_detail);

        tvCreateAt = (TextView) itemView.findViewById(R.id.tv_time_post);
//        cbIsVote = (CheckBox) itemView.findViewById(R.id.cb_isVote);
        ivIsVote = (ImageView) itemView.findViewById(R.id.iv_bookmark);

        String urlAvatar = itemTimeLine.getAvaAuthor();
        if (urlAvatar.isEmpty()) urlAvatar += "...";
        Picasso.with(mContext)
                .load(urlAvatar).fit()
                .placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
                .into(ivAvatar);

//        if (itemTimeLine.isIncognito()){
//            ivAvatar.setAlpha(0.2f);
//        }

        createListener();

        webView = (WebView) itemView.findViewById(R.id.webview);
    }

    public void setContentToWebview(String content) {
        String htmlData = "<html>"
                + "<head>"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />"
                + "</head>"
                + "<body>"
                + content
                + "</body>"
                + "</html>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent mIntent = new Intent();
                mIntent.setClass(mContext, WebviewActivity.class);
                mIntent.putExtra("url", url);
                mContext.startActivity(mIntent);
                return true;
            }

        });

        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) return false;

                WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
                switch (hr.getType()) {
                    case IMAGE_TYPE:
                        Log.i(TAG, "Image = " + hr.getExtra());
                        new ImageViewer.Builder(mContext, new String[]{hr.getExtra()})
                                .setStartPosition(0)
                                .show();
                        break;
                    case SRC_ANCHOR_TYPE:
                        Log.i(TAG, "Url = " + hr.getExtra());
                        break;
                }
                return false;
            }
        });
    }

    public void setCbIsVote() {
        boolean isPostByTeacher = itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher");
        boolean isSolve = itemTimeLine.isSolve();

        ivIsVote.setVisibility(View.GONE);

        if (isSolve) {
            ivIsVote.setVisibility(View.VISIBLE);
            ivIsVote.setImageResource(R.drawable.ic_bookmark_solved);
        }
        if (isPostByTeacher) {
            ivIsVote.setVisibility(View.VISIBLE);
            ivIsVote.setImageResource(R.drawable.ic_bookmark_post_teacher);
        }
    }

    private void createListener() {
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionManager.pVotePost(itemTimeLine.getIdAuthor(), userId))
                    Toast.makeText(mContext, "Bạn không thể tự đánh giá bài viết của chính mình", Toast.LENGTH_SHORT).show();
                else postLike(itemTimeLine.getIdPost(), 1);
            }
        });

        btnDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionManager.pVotePost(itemTimeLine.getIdAuthor(), userId))
                    Toast.makeText(mContext, "Bạn không thể tự đánh giá bài viết của chính mình", Toast.LENGTH_SHORT).show();
                else postLike(itemTimeLine.getIdPost(), -1);
            }
        });
    }

    @Override
    public int getViewHolderType() {
        return AbstractHolder.TYPE_HEADER;
    }

    //Post like and cmt to server
    private void postLike(String idPost, int content) {
        JSONObject params = new JSONObject();
        try {
            params.put("post_id", idPost);
            params.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(mContext, Request.Method.POST, AppConfig.URL_POST_LIKE, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    int countVote = response.getJSONObject("data").getInt("vote_count");
                    itemTimeLine.setLike(countVote);
                    tvLike.setText(itemTimeLine.getLike() + "");
                    if (itemTimeLine.getLike() >= 0) {
                        ivLike.setImageResource(R.drawable.ic_vote_up);
                    } else {
                        ivLike.setImageResource(R.drawable.ic_vote_down);
                    }
                }
            }
        });

        requestServer.sendRequest("vote");

        Intent mIntent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("item_timeline", itemTimeLine);
        mIntent.putExtras(b);
        PostDetailActivity postDetailActivity = (PostDetailActivity) mContext;
        postDetailActivity.setResult(Activity.RESULT_OK, mIntent);
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

    public ImageView getIvTypePost() {
        return ivTypePost;
    }
}
