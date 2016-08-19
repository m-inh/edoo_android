package com.fries.edoo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.fries.edoo.R;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PostWriterActivity extends AppCompatActivity {

    private static final String TAG = "PostWriterActivity";
    private ProgressDialog pDialog;

    private EditText edtContentPost;
    private EditText edtTitlePost;

    private String idLop;

    private SQLiteHandler sqlite;

    private boolean isAllowedClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_writer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get data from Mainactivity
        Intent mIntent = getIntent();
        this.idLop = mIntent.getStringExtra("class_id");

        sqlite = new SQLiteHandler(this);

        isAllowedClick = true;

        initViews();
    }

    private void initViews() {
        edtTitlePost = (EditText) findViewById(R.id.edt_titlePost);
        edtContentPost = (EditText) findViewById(R.id.edt_contentPost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.writepost_menu, menu);
        menu.findItem(R.id.action_post).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!isAllowedClick) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_post:
                String title = edtTitlePost.getText().toString();
                String content = edtContentPost.getText().toString();
                if (content.isEmpty()) {
                    Toast.makeText(PostWriterActivity.this, "Bài viết không có nội dung!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (title.isEmpty()) {
                    Toast.makeText(PostWriterActivity.this, "Bài viết không có tiêu đề!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //block button post when clicked
                isAllowedClick = false;

                //get intent tu MainActivity
//                Intent intent = getIntent();
                boolean isTeacher = (sqlite.getUserDetails().get("type").equalsIgnoreCase("teacher"));
                postPost(idLop, title, content, "note", false, isTeacher);
                break;
        }
        return true;
    }

    private void postPost(String classId, String title, String content, String type, boolean isIncognito, boolean isPostTeacher) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.show();

        String url = AppConfig.URL_POST_POST;

        JSONObject params = new JSONObject();
        try {
            params.put("class_id", classId);
            params.put("title", title);
            params.put("content", content);
            params.put("type", type);
            params.put("is_incognito", isIncognito);
            params.put("is_post_teacher", isPostTeacher);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(this, Request.Method.POST, url, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                pDialog.dismiss();
                if (!error) {
                    Log.i(TAG, response.toString());

                    Message msg = new Message();
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                } else {
//                    ivPost.setClickable(true);
                    isAllowedClick = true;
                    Log.i(TAG, "Post error: " + message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });

        requestServer.sendRequest("post new post");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setResult(RESULT_OK);
            finish();
        }
    };

}
