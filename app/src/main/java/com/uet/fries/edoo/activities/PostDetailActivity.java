package com.uet.fries.edoo.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.adapter.EventExerciseDetailAdapter;
import com.uet.fries.edoo.adapter.PostDetailAdapter;
import com.uet.fries.edoo.adapter.TimeLineAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.holder.AbstractHolder;
import com.uet.fries.edoo.models.ItemComment;
import com.uet.fries.edoo.models.ItemTimeLine;
import com.uet.fries.edoo.utils.CommonVLs;
import com.uet.fries.edoo.utils.PermissionManager;
import com.uet.fries.edoo.utils.Reporter;

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
    private static final int REQUEST_EDIT_POST = 12112;

    private ProgressDialog pDialog;

    private RecyclerView rvMain;
    private RecyclerView.Adapter<AbstractHolder> mAdapter;

    private EditText edtComment;
    private ImageView btnSend;
    private ItemTimeLine itemTimeline;
    private boolean postIsChanged;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Reporter.register(this);// Crash Reporter

        setContentView(R.layout.activity_post_detailt);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);

        Intent mIntent = getIntent();
        this.itemTimeline = (ItemTimeLine) mIntent.getSerializableExtra("timelineItem");

//        if (this.itemTimeline != null) {
//            initViews(this.itemTimeline);
//        } else {
//            initViews();
//        }

        initViews(this.itemTimeline);

        if (itemTimeline == null) {
            postIsChanged = true;
        } else postIsChanged = false;
        getPostDetail(mIntent.getStringExtra("post_id"));
    }

    private void initViews() {
        rvMain = (RecyclerView) findViewById(R.id.rv_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMain.setLayoutManager(linearLayoutManager);
        edtComment = (EditText) findViewById(R.id.edt_comment);
        btnSend = (ImageView) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PostDetailActivity.this.itemTimeline != null && !edtComment.getText().toString().isEmpty()) {
                    postCmt(itemTimeline.getIdPost(), edtComment.getText().toString());
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Nhập câu trả lời trước!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews(final ItemTimeLine itemTimeline) {
        initViews();

        if (!itemTimeline.getType().equalsIgnoreCase(ItemTimeLine.TYPE_POST_EXERCISE)) {
            mAdapter = new PostDetailAdapter(this, itemTimeline);
        } else {
            mAdapter = new EventExerciseDetailAdapter(this, itemTimeline);
        }
        rvMain.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_details_menu, menu);
        MenuItem mnDeletePost = menu.findItem(R.id.action_delete_post);
        MenuItem mnEditPost = menu.findItem(R.id.action_edit_post);
        HashMap<String, String> user = new SQLiteHandler(this).getUserDetails();
//        boolean userIsTeacher = user.get("type").equalsIgnoreCase("teacher");
//        boolean authorIsTeacher = itemTimeline.getTypeAuthor().equalsIgnoreCase("teacher");
//        boolean userIsAuthor = user.get("uid").equalsIgnoreCase(itemTimeline.getIdAuthor());

        // Permission of User
//        boolean permission = PermissionManager.pDeletePost(userIsTeacher, authorIsTeacher, userIsAuthor);
        if (itemTimeline != null) {
            boolean permissionDelete = PermissionManager.pDeletePost(
                    itemTimeline.getIdAuthor(),
                    itemTimeline.getTypeAuthor(),
                    user.get("uid"),
                    user.get("type"));
            mnDeletePost.setVisible(permissionDelete);
            boolean permissionEdit = PermissionManager.pEditPost(
                    itemTimeline.getIdAuthor(),
                    user.get("uid"));
            mnEditPost.setVisible(permissionEdit);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete_post:
                showDialogDeletePost();
                break;
            case R.id.action_edit_post:
                Intent mIntent = new Intent();
                mIntent.putExtra("timelineItem", itemTimeline);
                mIntent.putExtra("post_id", itemTimeline.getIdPost());
                mIntent.setClass(this, PostWriterActivity.class);
                startActivityForResult(mIntent, REQUEST_EDIT_POST);
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

    private void showDialogDeletePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa bài đăng");
        builder.setMessage(getResources().getString(R.string.txt_question_delete_post));
        builder.setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestDeletePost();
            }
        });
        builder.setNegativeButton(getString(R.string.txt_no), null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            postIsChanged = true;
            getPostDetail(itemTimeline.getIdPost());

        }
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

                    JSONObject jsonPost = response.getJSONObject("data");

                    String id = jsonPost.getString("id");
                    String titlePost = jsonPost.getString("title");
                    String contentPost = jsonPost.getString("content");
                    String desPost = jsonPost.getString("description");
                    int like = jsonPost.getInt("vote_count");
                    int commentCount = jsonPost.getInt("comment_count");
                    boolean isIncognito = jsonPost.getInt("is_incognito") == 1;
//                    boolean isSeen = jsonPost.getInt("is_seen") == 1;
                    boolean isSeen = true;
                    boolean isPostSolve = jsonPost.getInt("is_solve") == 1;
                    String timeCreateAtPost = jsonPost.getString("created_at");
                    String type = jsonPost.getString("type");

                    //author post
                    String nameAuthorPost = "Ẩn danh";
                    String idAuthorPost = "";
                    String emailAuthorPost = "";
                    String typeAuthorPost = "";
                    String mssvAuthorPost = "";
                    String avarAuthorPost = "okmen.com";

                    try {
                        JSONObject jsonAuthorPost = jsonPost.getJSONObject("author");
                        idAuthorPost = jsonAuthorPost.getString("id");
                        emailAuthorPost = jsonAuthorPost.getString("email");
                        typeAuthorPost = jsonAuthorPost.getString("capability");
                        mssvAuthorPost = jsonAuthorPost.getString("code");
                        avarAuthorPost = jsonAuthorPost.getString("avatar");
                        nameAuthorPost = jsonAuthorPost.getString("name");

                        if (isIncognito) {
                            nameAuthorPost = "Ẩn danh";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    boolean isConfirm = false;
                    final ItemTimeLine itemTimeLine = new ItemTimeLine(id, titlePost, nameAuthorPost, avarAuthorPost, isIncognito, contentPost, like, isConfirm, type);
                    itemTimeLine.setTypeAuthor(typeAuthorPost);
                    itemTimeLine.setDescription(desPost);
                    itemTimeLine.setIdAuthor(idAuthorPost);
                    itemTimeLine.setCommentCount(commentCount);
                    itemTimeLine.setIsSeen(isSeen);
                    itemTimeLine.setSolve(isPostSolve);

                    String format = CommonVLs.TIME_FORMAT;
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    try {
                        String tempTime = DateFormat.format("dd/MM/yy", sdf.parse(timeCreateAtPost)
                                .getTime())
                                .toString();
                        itemTimeLine.setCreateAt(tempTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // parse cmt
                    final ArrayList<ItemComment> cmtArr = new ArrayList<>();

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
                                avarAuthorComment, contentComment, isSolve, typeAuthorComment);

                        itemComment.setCreateAt(CommonVLs.convertDate(timeCreateAtCmt));

                        cmtArr.add(itemComment);
                    }

                    itemTimeLine.setItemComments(cmtArr);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (postIsChanged) {
                                PostDetailActivity.this.itemTimeline = itemTimeLine;
                                String type = itemTimeLine.getType();
                                if (!type.equalsIgnoreCase(ItemTimeLine.TYPE_POST_EXERCISE)) {
                                    ((PostDetailAdapter) mAdapter).setItemTimeline(PostDetailActivity.this.itemTimeline);
                                } else {
                                    ((EventExerciseDetailAdapter) mAdapter).setItemTimeline(PostDetailActivity.this.itemTimeline);
                                }
                                postIsChanged = false;
                                Intent mIntent = new Intent();
                                Bundle b = new Bundle();
                                b.putSerializable("item_timeline", itemTimeline);
                                mIntent.putExtras(b);
                                setResult(RESULT_OK, mIntent);
                            } else {
                                String type = itemTimeLine.getType();
                                if (!type.equalsIgnoreCase(ItemTimeLine.TYPE_POST_EXERCISE)) {
                                    ((PostDetailAdapter) mAdapter).setItemComments(cmtArr);
                                } else {
                                    ((EventExerciseDetailAdapter) mAdapter).setItemComments(cmtArr);
                                }
                            }
                        }
                    });
                }
            }
        });

        requestServer.sendRequest("get post detail");
    }

    // -------------------------------------------------
    private void postCmt(final String post, final String content) {
//        Log.i(TAG, "idpost " + post);
//        Log.i(TAG, "content " + content);

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
                    String capability = user.get("type");
                    // Thay doi tren Local
                    itemTimeline.getItemComments().add(new ItemComment(idCmt, uid, name, ava, content, false, capability));
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

    // -------------------------------------------
    private void requestDeletePost() {
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
                if (!error) {
//                    Log.i(TAG, "Delete post response: " + response.toString());
                    setResult(RESULT_DELETE_COMPLETE);
                    PostDetailActivity.this.finish();
                }
            }
        });
        requestServer.sendRequest("delete_post");
    }

}
