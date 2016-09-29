package com.uet.fries.edoo.holder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.ListSubmittedExercise;
import com.uet.fries.edoo.activities.PostDetailActivity;
import com.uet.fries.edoo.activities.WebviewActivity;
import com.uet.fries.edoo.adapter.PostDetailAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.MultipartRequest;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.models.ItemTimeLine;
import com.uet.fries.edoo.utils.CommonVLs;
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
    private String userId, userType;
    private Context mContext;

    private TextView tvTitle;
    private TextView tvContent;
    private WebView webView;
    private TextView tvAuthorName;
    private CircleImageView ivAvatar;
    private TextView tvCreateAt, tvDeadline, btnSubmitCheckExercise, tvPercentSubmitted;

    public ItemEventDetailHolder(View itemView) {
        super(itemView);
    }

    public ItemEventDetailHolder(View itemView, ItemTimeLine itemTimeLine, String userId, String userType) {
        this(itemView);
        this.itemTimeLine = itemTimeLine;
        this.userId = userId;
        this.userType = userType;
        this.mContext = itemView.getContext();

        ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_nameauthor);

        tvCreateAt = (TextView) itemView.findViewById(R.id.tv_time_post);
        tvDeadline = (TextView) itemView.findViewById(R.id.tv_deadline);
        btnSubmitCheckExercise = (TextView) itemView.findViewById(R.id.btn_submit_check_exercise);
        tvPercentSubmitted = (TextView) itemView.findViewById(R.id.tv_percent_submit);

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
        if (userType.equalsIgnoreCase("teacher")) {
            btnSubmitCheckExercise.setText(mContext.getString(R.string.txt_check_submit));
        } else {
            btnSubmitCheckExercise.setText(mContext.getString(R.string.txt_submit));
        }

        btnSubmitCheckExercise.setOnClickListener(click);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (userType.equalsIgnoreCase("teacher")) {
                checkSubmitExercise();
            } else {
                pickImage();
            }
        }
    };

    // ---------------------------------------------------------------------------
    /**
     * pick image
     */

    private void pickImage() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Gửi bài tập từ:");
        CharSequence[] selection = {mContext.getString(R.string.txt_gallery), mContext.getString(R.string.txt_camera)};
        dialog.setItems(selection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0) ((PostDetailActivity)mContext).pickImageFromMemory();
                else ((PostDetailActivity)mContext).pickImageFromCamera();
            }
        });
        dialog.show();
    }

    private void checkSubmitExercise(){
        Intent intent = new Intent(mContext, ListSubmittedExercise.class);
        intent.putExtra("post_id", itemTimeLine.getIdPost());
        mContext.startActivity(intent);
    }


    //---------------------------------------------------------------
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

    public void setPercentSubmitted(String percent){
        tvPercentSubmitted.setText(percent);
    }
}
