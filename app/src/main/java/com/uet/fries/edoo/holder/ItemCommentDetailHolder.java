package com.uet.fries.edoo.holder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.PostDetailActivity;
import com.uet.fries.edoo.activities.WebviewActivity;
import com.uet.fries.edoo.adapter.PostDetailAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.models.ITimelineBase;
import com.uet.fries.edoo.models.ItemComment;
import com.uet.fries.edoo.models.ItemTimeLine;
import com.uet.fries.edoo.utils.PermissionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class ItemCommentDetailHolder extends AbstractHolder {

    private static final String TAG = ItemCommentDetailHolder.class.getSimpleName();
    private Context mContext;
    private ITimelineBase itemTimeline;
    private ItemComment itemComment;

    private TextView tvAuthorName;
    private CircleImageView ivAuthorAvatar;
//    private TextView tvComment;
    private WebView wvCmt;
    private TextView tvCreateAt;
    private ImageView ivCommentSolved, ivCommentMenu;
    private RecyclerView.Adapter<AbstractHolder> postDetailAdapter;

    public ItemCommentDetailHolder(View itemView) {
        super(itemView);

        this.mContext = itemView.getContext();

        tvAuthorName = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_authorname);
        ivAuthorAvatar = (CircleImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_avatar);
//        tvComment = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_comment);
        wvCmt = (WebView) itemView.findViewById(R.id.wv_comment);
        ivCommentMenu = (ImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_comment_menu);
        ivCommentSolved = (ImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_comment_solved);
        tvCreateAt = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_create_at);
    }

    public ItemCommentDetailHolder(View view, ITimelineBase itemTimeline) {
        this(view);
        this.itemTimeline = itemTimeline;
    }

    public ItemCommentDetailHolder(View view, ITimelineBase itemTimeline, RecyclerView.Adapter<AbstractHolder> postDetailAdapter) {
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

    public void setItemComment(final ItemComment itemComment, final String userId, final String userType) {
        this.itemComment = itemComment;

        tvAuthorName.setText(itemComment.getName());
//        tvComment.setText(itemComment.getContent());
        setContentToWebview(itemComment.getContent());
        tvCreateAt.setText(itemComment.getCreateAt());
        if (!itemComment.getAvaUrl().isEmpty()) {
            Picasso.with(mContext)
                    .load(itemComment.getAvaUrl()).fit()
                    .placeholder(com.uet.fries.edoo.R.mipmap.ic_user).error(com.uet.fries.edoo.R.mipmap.ic_user)
                    .into(ivAuthorAvatar);
        }

        updateIvIsSolved();

        ivCommentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuComment(userId, userType);
            }
        });
    }

    public void setContentToWebview(String content){
        String htmlData = "<html>"
                + "<head>"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />"
                + "</head>"
                + "<body>"
                + content
                + "</body>"
                + "</html>";
        wvCmt.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);

        wvCmt.setWebViewClient(new WebViewClient(){
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

    private void showMenuComment(String userId, String userType) {
        PopupMenu menu = new PopupMenu(mContext, ivCommentMenu);
        menu.getMenuInflater().inflate(com.uet.fries.edoo.R.menu.comment_menu, menu.getMenu());

        MenuItem itSolve = menu.getMenu().findItem(com.uet.fries.edoo.R.id.action_solve_comment);
        MenuItem itNotSolve = menu.getMenu().findItem(com.uet.fries.edoo.R.id.action_not_solve_comment);
        MenuItem itDeleteComment = menu.getMenu().findItem(com.uet.fries.edoo.R.id.action_delete_comment);

        boolean permissionDeleteComment = PermissionManager.pDeleteComment(
                itemTimeline.getIdAuthor(),
                itemComment.getCapability(),
                itemComment.getIdAuthorComment(),
                userId,
                userType
        );
        itDeleteComment.setVisible(permissionDeleteComment);


        boolean permissionSolveComment = PermissionManager.pSolveComment(
                itemTimeline.getIdAuthor(),
                userId,
                userType,
                itemComment.getIdAuthorComment()
        );

        if (permissionSolveComment) {
            itSolve.setVisible(!itemComment.isSolved());
            itNotSolve.setVisible(itemComment.isSolved());
        } else {
            itSolve.setVisible(false);
            itNotSolve.setVisible(false);
        }

        // Bai tap thi se khong co solve, notSolve
        if (itemTimeline.getType().equalsIgnoreCase(ITimelineBase.TYPE_POST_EXERCISE)){
            itSolve.setVisible(false);
            itNotSolve.setVisible(false);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case com.uet.fries.edoo.R.id.action_solve_comment:
                        Toast.makeText(mContext, "Solved comment", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < itemTimeline.getItemComments().size(); i++) {
                            itemTimeline.getItemComments().get(i).setIsSolved(false);
                        }
                        postSolve(itemComment.getIdComment(), true);
                        break;
                    case com.uet.fries.edoo.R.id.action_not_solve_comment:
                        Toast.makeText(mContext, "Remove Solved comment", Toast.LENGTH_SHORT).show();
                        postSolve(itemComment.getIdComment(), false);
                        break;
                    case com.uet.fries.edoo.R.id.action_delete_comment:
                        showDialogDeleteComment();
                        break;
                }
                return true;
            }
        });
        menu.show();
    }

    public void updateIvIsSolved() {
        if (itemComment.isSolved()) ivCommentSolved.setVisibility(View.VISIBLE);
        else ivCommentSolved.setVisibility(View.GONE);
    }

    private void showDialogDeleteComment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(com.uet.fries.edoo.R.string.warn));
        builder.setMessage(mContext.getResources().getString(com.uet.fries.edoo.R.string.txt_question_delete_post));
        builder.setPositiveButton(mContext.getResources().getString(com.uet.fries.edoo.R.string.txt_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestDeleteComment();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.txt_no), null);
        builder.show();
    }

    /**
     * Post to server: Solved - Not Solved
     *
     * @param idCmt    id of Comment
     * @param isSolved true: Solved, false: Not Solved
     */
    public void postSolve(final String idCmt, final boolean isSolved) {
        Log.i(TAG, idCmt);

        String url;
        if (isSolved) url = AppConfig.URL_SOLVE_COMMENT;
        else url = AppConfig.URL_UNSOLVE_COMMENT;
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
                if (!error) {
                    Log.d(TAG, response.toString());

                    ((ItemTimeLine)itemTimeline).setSolve(isSolved);
                    if (isSolved) ((PostDetailAdapter)postDetailAdapter).setSolveCmt(idCmt);
                    else ((PostDetailAdapter)postDetailAdapter).setUnsolveCmt();
                }
                Log.d(TAG, message);
            }
        });

        requestServer.sendRequest("Post solve_unsolve");

        Intent mIntent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("item_timeline", itemTimeline);
        mIntent.putExtras(b);
        PostDetailActivity postDetailActivity = (PostDetailActivity) mContext;
        postDetailActivity.setResult(Activity.RESULT_OK, mIntent);
    }

    private void requestDeleteComment() {
        final String idComment = itemComment.getIdComment();
        JSONObject params = new JSONObject();
        try {
            params.put("comment_id", idComment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestServer requestServer = new RequestServer(mContext, Request.Method.POST, AppConfig.URL_DELETE_COMMENT, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    JSONObject data = response.getJSONObject("data");
                    Log.i(TAG, "delete comment response = " + data.toString());

                    itemTimeline.deleteComment(idComment);
                    itemTimeline.setCommentCount(itemTimeline.getCommentCount() - 1);
                    postDetailAdapter.notifyDataSetChanged();
                }
            }
        });
        requestServer.sendRequest("delete_comment");

        Intent mIntent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("item_timeline", itemTimeline);
        mIntent.putExtras(b);
        PostDetailActivity postDetailActivity = (PostDetailActivity) mContext;
        postDetailActivity.setResult(Activity.RESULT_OK, mIntent);
    }

    private void setIsSolved(boolean isSolved) {
        this.itemComment.setIsSolved(isSolved);
    }

    public ImageView getIvCommentSolved() {
        return ivCommentSolved;
    }

    public ImageView getIvCommentMenu() {
        return ivCommentMenu;
    }

    public TextView getTvCreateAt() {
        return tvCreateAt;
    }
}
