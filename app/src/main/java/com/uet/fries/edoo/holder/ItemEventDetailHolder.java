package com.uet.fries.edoo.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.squareup.picasso.Picasso;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.PostDetailActivity;
import com.uet.fries.edoo.activities.WebviewActivity;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.models.ItemTimeLine;
import com.uet.fries.edoo.utils.PermissionManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class ItemEventDetailHolder extends AbstractHolder {

    private static final String TAG = "ItemPostDetailHolder";
    private ItemTimeLine itemTimeLine;
    private String userId;
    private Context mContext;

    private TextView tvTitle;
    private TextView tvContent;
    private WebView webView;
    private TextView tvAuthorName;
    private CircleImageView ivAvatar;
    private TextView tvCreateAt, tvDeadline, btnSubmitCheckExercise;

    public ItemEventDetailHolder(View itemView) {
        super(itemView);
    }

    public ItemEventDetailHolder(View itemView, ItemTimeLine itemTimeLine, String userId) {
        this(itemView);
        this.itemTimeLine = itemTimeLine;
        this.userId = userId;
        this.mContext = itemView.getContext();

        ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_nameauthor);

        tvCreateAt = (TextView) itemView.findViewById(R.id.tv_time_post);
        tvDeadline = (TextView) itemView.findViewById(R.id.tv_deadline);
        btnSubmitCheckExercise = (TextView) itemView.findViewById(R.id.btn_submit_check_exercise);

        Picasso.with(mContext)
                .load(itemTimeLine.getAva()).fit()
                .placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user)
                .into(ivAvatar);

//        if (itemTimeLine.isIncognito()){
//            ivAvatar.setAlpha(0.2f);
//        }

        webView = (WebView) itemView.findViewById(R.id.webview);

        setData();
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

    }

    public void setData() {
        if (itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher")) {
            btnSubmitCheckExercise.setText(mContext.getString(R.string.txt_check_submit));
        } else {
            btnSubmitCheckExercise.setText(mContext.getString(R.string.txt_submit));
        }

        btnSubmitCheckExercise.setOnClickListener(click);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemTimeLine.getTypeAuthor().equalsIgnoreCase("teacher")) {

            } else {

            }
        }
    };


    @Override
    public int getViewHolderType() {
        return 0;
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

    public TextView getTvCreateAt() {
        return tvCreateAt;
    }

    public void setDeadline(String deadline) {
        tvDeadline.setText(deadline);
    }
}
