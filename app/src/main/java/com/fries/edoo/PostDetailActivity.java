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
import com.fries.edoo.fragment.LopKhoaHocFragment;
import com.fries.edoo.fragment.LopMonHocFragment;
import com.fries.edoo.fragment.NhomFragment;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;

import org.json.JSONException;
import org.json.JSONObject;

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
//        toolbar.setTitle("ok");

        pDialog = new ProgressDialog(this);

        Intent mIntent = getIntent();
        this.itemTimeline = (ItemTimeLine) mIntent.getSerializableExtra("timelineItem");

        Log.i(TAG, itemTimeline.getTitle());
        Log.i(TAG, itemTimeline.getContent());
        Log.i(TAG, itemTimeline.getIdPost());
        Log.i(TAG, itemTimeline.getKeyLopType());

        initViews(itemTimeline);
    }

    private void initViews(final ItemTimeLine itemTimeline) {
        switch (itemTimeline.getKeyLopType()) {
            case LopMonHocFragment.KEY_LOP_MON_HOC:
                toolbar.setTitle("Lớp môn học");
                Log.i(TAG, "lop mon hoc");
                break;
            case LopKhoaHocFragment.KEY_LOP_KHOA_HOC:
                toolbar.setTitle("Lớp khoá học");
                Log.i(TAG, "lop khoa hoc");
                break;
            case NhomFragment.KEY_NHOM:
                toolbar.setTitle("Nhom");
                break;
            default:
                break;
        }
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
                SQLiteHandler db = new SQLiteHandler(PostDetailActivity.this);
                HashMap<String, String> user = db.getUserDetails();
                String uid = user.get("uid");
                String name = user.get("name");

                if (!edtComment.getText().toString().isEmpty()) {
                    postCmt(itemTimeline.getKeyLopType(), itemTimeline.getIdPost(), uid, edtComment.getText().toString());
                } else {
                    Toast.makeText(PostDetailActivity.this, "Nhập câu trả lời trước!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_detail, menu);
//        Log.i(TAG, "ok");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void postCmt(final String base, final String post, final String author, final String content) {
        showDialog();
        Log.i(TAG, "base " + base);
        Log.i(TAG, "idpost " + post);
        Log.i(TAG, "author " + author);
        Log.i(TAG, "content " + content);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_POST_COMMENT,
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
                                JSONObject jsonComment = jsonObject.getJSONObject("comment");
                                String idCmt = jsonComment.getString("id");

                                Bundle b = new Bundle();
                                b.putString("idCmt", idCmt);
                                b.putString("content", content);

                                Message msg = new Message();
                                msg.setData(b);
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
                Log.i(TAG, "Post error: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("author", author);
                data.put("base", base);
                data.put("content", content);
                data.put("post", post);

                return data;
            }
        };

        AppController.getInstance().addToRequestQueue(request, "post");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(PostDetailActivity.this, "Gửi bình luận thành công", Toast.LENGTH_LONG).show();
            edtComment.setText("");

            Bundle b = msg.getData();
            String idCmt = b.getString("idCmt");
            String content = b.getString("content");

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
            mAdapter.setItemTimeline(itemTimeline);

            setResult(RESULT_OK);
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
