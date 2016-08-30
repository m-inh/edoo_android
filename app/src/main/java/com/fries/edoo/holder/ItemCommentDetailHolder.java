package com.fries.edoo.holder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.fries.edoo.utils.PermissonManager;
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

    private static final String TAG = ItemCommentDetailHolder.class.getSimpleName();
    private Context mContext;
    private ItemTimeLine itemTimeline;
    private ItemComment itemComment;

    private TextView tvAuthorName;
    private CircleImageView ivAuthorAvatar;
    private TextView tvComment;
    private TextView tvCreateAt;
    private ImageView ivCommentSolved, ivCommentMenu;
    private PostDetailAdapter postDetailAdapter;

    public ItemCommentDetailHolder(View itemView) {
        super(itemView);

        this.mContext = itemView.getContext();

        tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorname);
        ivAuthorAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        ivCommentMenu = (ImageView) itemView.findViewById(R.id.iv_comment_menu);
        ivCommentSolved = (ImageView) itemView.findViewById(R.id.iv_comment_solved);
        tvCreateAt = (TextView) itemView.findViewById(R.id.tv_create_at);
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

    public void setItemComment(final ItemComment itemComment, final String userId, final String userType) {
        this.itemComment = itemComment;

        tvAuthorName.setText(itemComment.getName());
        tvComment.setText(itemComment.getContent());
        tvCreateAt.setText(itemComment.getCreateAt());
        if (!itemComment.getAvaUrl().isEmpty()) {
            Picasso.with(mContext)
                    .load(itemComment.getAvaUrl()).fit()
                    .placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user)
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

    private void showMenuComment(String userId, String userType) {
        PopupMenu menu = new PopupMenu(mContext, ivCommentMenu);
        menu.getMenuInflater().inflate(R.menu.comment_menu, menu.getMenu());

        MenuItem itSolve = menu.getMenu().findItem(R.id.action_solve_comment);
        MenuItem itNotSolve = menu.getMenu().findItem(R.id.action_not_solve_comment);
        MenuItem itDeleteComment = menu.getMenu().findItem(R.id.action_delete_comment);

        boolean permissionDeleteComment = PermissonManager.pDeleteComment(
                itemTimeline.getIdAuthor(),
                itemComment.getCapability(),
                userId,
                userType
        );
        itDeleteComment.setVisible(permissionDeleteComment);


        boolean permissionSolveComment = PermissonManager.pSolveComment(
                itemTimeline.getIdAuthor(),
                userId,
                userType,
                itemComment.getIdAuthorComment()
        );

        if (permissionSolveComment){
            itSolve.setVisible(!itemComment.isSolved());
            itNotSolve.setVisible(itemComment.isSolved());
        } else {
            itSolve.setVisible(false);
            itNotSolve.setVisible(false);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_solve_comment:
                        Toast.makeText(mContext, "Solved comment", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < itemTimeline.getItemComments().size(); i++) {
                            itemTimeline.getItemComments().get(i).setIsSolved(false);
                        }
                        postSolve(itemComment.getIdComment(), true);
                        break;
                    case R.id.action_not_solve_comment:
                        Toast.makeText(mContext, "Remove Solved comment", Toast.LENGTH_SHORT).show();
                        postSolve(itemComment.getIdComment(), false);
                        break;
                    case R.id.action_delete_comment:
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

    private void showDialogDeleteComment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.warn));
        builder.setMessage(mContext.getResources().getString(R.string.txt_question_delete_post));
        builder.setPositiveButton(mContext.getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestDeleteComment();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.txt_cancel), null);
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

                    itemTimeline.setSolve(isSolved);
                    if (isSolved) postDetailAdapter.setSolveCmt(idCmt);
                    else postDetailAdapter.setUnsolveCmt();
                }
                Log.d(TAG, message);
            }
        });

        requestServer.sendRequest("Post solve_unsolve");
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
                    itemTimeline.setCommentCount(itemTimeline.getCommentCount()-1);
                    postDetailAdapter.notifyDataSetChanged();
                }
            }
        });
        requestServer.sendRequest("delete_comment");
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
