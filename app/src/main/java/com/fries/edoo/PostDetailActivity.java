package com.fries.edoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.adapter.PostDetailAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.fragment.LopKhoaHocFragment;
import com.fries.edoo.fragment.LopMonHocFragment;
import com.fries.edoo.fragment.NhomFragment;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TooNies1810 on 2/19/16.
 */
public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailActivity";

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

        edtComment = (EditText) findViewById(R.id.edt_comment);
        btnSend = (ImageView) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtComment.getText().toString().isEmpty()) {
                    postCmt(itemTimeline.getIdPost(), edtComment.getText().toString());
                } else {
                    Toast.makeText(PostDetailActivity.this, "Nhập câu trả lời trước!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

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

                        Log.i(TAG, "comment: " + nameAuthorComment);
                        Log.i(TAG, "comment: " + emailAuthorComment);
                        Log.i(TAG, "comment: " + typeAuthorComment);

//                        boolean isVote = cmtJson.getBoolean("confirmed");

//                        if (isVote || typeAuthorComment.equalsIgnoreCase("teacher")) {
//                            isConfirm = true;
//                            ((ItemTimeLine) itemPostArr.get(i)).setIsConfirmByTeacher(true);
//                        }

                        cmtArr.add(new ItemComment(idComment, idAuthorComment, nameAuthorComment, avarAuthorComment, contentComment, false));
                    }
                    mAdapter.setItemComments(cmtArr);
                }
            }
        });

        requestServer.sendRequest("get post detail");
    }

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
                if (!error){
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
}
