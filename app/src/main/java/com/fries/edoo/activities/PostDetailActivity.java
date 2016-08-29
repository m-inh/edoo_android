package com.fries.edoo.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fries.edoo.R;
import com.fries.edoo.adapter.PostDetailAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;
import com.fries.edoo.utils.CommonVLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = PostDetailActivity.class.getSimpleName();
    public static final int RESULT_DELETE_COMPLETE = 12;

    private ProgressDialog pDialog;

    private RecyclerView rvMain;
    private PostDetailAdapter mAdapter;

    private EditText edtComment;
    private ImageView btnSend;
    private ItemTimeLine itemTimeline;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detailt);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);

        Intent mIntent = getIntent();
        this.itemTimeline = (ItemTimeLine) mIntent.getSerializableExtra("timelineItem");

        initViews(itemTimeline);

        getPostDetail(itemTimeline.getIdPost());
    }

    private void initViews(final ItemTimeLine itemTimeline) {
        rvMain = (RecyclerView) findViewById(R.id.rv_main);
        mAdapter = new PostDetailAdapter(this, itemTimeline);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMain.setLayoutManager(linearLayoutManager);
        rvMain.setAdapter(mAdapter);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            rvMain.setOnScrollChangeListener((View.OnScrollChangeListener) onScrollListener);
//        }

        edtComment = (EditText) findViewById(R.id.edt_comment);
        btnSend = (ImageView) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtComment.getText().toString().isEmpty()) {
                    postCmt(itemTimeline.getIdPost(), edtComment.getText().toString());
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Nhập câu trả lời trước!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete_post:
//                Toast.makeText(this, "Delete post: Coming soon", Toast.LENGTH_SHORT).show();
                showDialogDeletePost();
                break;
            case R.id.action_edit_post:
                Toast.makeText(this, "Edit post: Coming soon", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        toolbar.setVisibility(View.VISIBLE);
    }

    private void showDialogDeletePost(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.warn));
        builder.setMessage(getResources().getString(R.string.txt_question_delete_post));
        builder.setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestDeletePost();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.txt_cancel), null);
        builder.show();
    }

    // -------------------------------- RequestServer ----------------------------------------------

    private void getPostDetail(String idPost) {
        String url = AppConfig.URL_GET_POST_DETAIL + "/" + idPost;
        RequestServer requestServer = new RequestServer(this, Request.Method.GET, url);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    Log.i(TAG, response.toString());

                    ArrayList<ItemComment> cmtArr = new ArrayList<ItemComment>();

                    JSONArray commentJsonArr = response.getJSONObject("data").getJSONArray("comments");
                    for (int i = 0; i < commentJsonArr.length(); i++) {
                        JSONObject cmtJson = commentJsonArr.getJSONObject(i);

                        String idComment = cmtJson.getString("id");
                        String contentComment = cmtJson.getString("content");
                        String timeCreateAtCmt = cmtJson.getString("created_at");
                        boolean isSolve = cmtJson.getInt("is_solve") == 1;

                        String idAuthorComment = "";
                        String nameAuthorComment = "";
                        String emailAuthorComment = "";
                        String typeAuthorComment = "";
                        String mssvAuthorComment = "";
                        String avarAuthorComment = "";

                        try {
                            JSONObject jsonAuthorComment = cmtJson.getJSONObject("author");
                            idAuthorComment = jsonAuthorComment.getString("id");
                            nameAuthorComment = jsonAuthorComment.getString("name");
                            emailAuthorComment = jsonAuthorComment.getString("email");
                            typeAuthorComment = jsonAuthorComment.getString("capability");
                            mssvAuthorComment = jsonAuthorComment.getString("code");
                            avarAuthorComment = jsonAuthorComment.getString("avatar");
                        } catch (Exception e) {
                            continue;
                        }

                        ItemComment itemComment = new ItemComment(idComment,
                                idAuthorComment, nameAuthorComment,
                                avarAuthorComment, contentComment, isSolve);

                        itemComment.setCreateAt(CommonVLs.convertDate(timeCreateAtCmt));

                        cmtArr.add(itemComment);
                    }
                    mAdapter.setItemComments(cmtArr);
                }
            }
        });

        requestServer.sendRequest("get post detail");
    }

    // -------------------------------------------------
    private void postCmt(final String post, final String content) {
        Log.i(TAG, "idpost " + post);
        Log.i(TAG, "content " + content);

        String url = AppConfig.URL_POST_COMMENT;
        JSONObject params = new JSONObject();
        try {
            params.put("post_id", post);
            params.put("content", content);
            params.put("is_incognito", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(this, Request.Method.POST, url, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    Log.d(TAG, response.toString());
                    String idCmt = response.getJSONObject("data").getString("id");

                    SQLiteHandler db = new SQLiteHandler(PostDetailActivity.this);
                    HashMap<String, String> user = db.getUserDetails();
                    String uid = user.get("uid");
                    String name = user.get("name");
                    String ava = user.get("avatar");
                    // Thay doi tren Local
                    itemTimeline.getItemComments().add(new ItemComment(idCmt, uid, name, ava, content, false));
                    if (user.get("type").equalsIgnoreCase("teacher")) {
                        itemTimeline.setIsConfirmByTeacher(true);
                    }

                    itemTimeline.setCommentCount(itemTimeline.getCommentCount() + 1);
                    mAdapter.notifyDataSetChanged();
                    rvMain.smoothScrollToPosition(mAdapter.getItemCount());
                    rvMain.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.a_b_l_post_details);
                            appBarLayout.setExpanded(false, true);
                        }
                    }, 1000);
                }
            }
        });

        requestServer.sendRequest("post a cmt");

        edtComment.setText("");

        Intent mIntent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("item_timeline", itemTimeline);
        mIntent.putExtras(b);
        setResult(RESULT_OK, mIntent);
    }

//    public RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
//        boolean hideToolBar = false;
//
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            if (hideToolBar) {
//                PostDetailActivity.this.getSupportActionBar().hide();
//            } else {
//                PostDetailActivity.this.getSupportActionBar().show();
//            }
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            if (dy > 20) {
//                hideToolBar = true;
//
//            } else if (dy < -5) {
//                hideToolBar = false;
//            }
//        }
//    };

    // -------------------------------------------
    private void requestDeletePost(){
        JSONObject params = new JSONObject();
        try {
            params.put("post_id", itemTimeline.getIdPost());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestServer requestServer = new RequestServer(this, Request.Method.POST, AppConfig.URL_DELETE_POST, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error){
                    Log.i(TAG, "Delete post response: " + response.toString());
                    setResult(RESULT_DELETE_COMPLETE);
                    PostDetailActivity.this.finish();
                }
            }
        });
        requestServer.sendRequest("delete_post");
    }

}
