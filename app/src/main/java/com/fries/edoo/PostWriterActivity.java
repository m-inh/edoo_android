package com.fries.edoo;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostWriterActivity extends AppCompatActivity {

    private static final String TAG = "PostWriterActivity";
    private ProgressDialog pDialog;

    //    private ImageView ivPost;
    private EditText edtContentPost;
    private EditText edtTitlePost;

    private String idLop;
    private String keyLopType;

    private boolean isAllowedClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_writer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //get data from Mainactivity
        Intent mIntent = getIntent();
        this.idLop = mIntent.getStringExtra("idLop");
        this.keyLopType = mIntent.getStringExtra("keyLopType");

        isAllowedClick = true;

        initViews();
    }

    private void initViews() {
        edtTitlePost = (EditText) findViewById(R.id.edt_titlePost);
        edtContentPost = (EditText) findViewById(R.id.edt_contentPost);
//        ivPost = (ImageView) findViewById(R.id.iv_post);

//        ivPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String title = edtTitlePost.getText().toString();
//                String content = edtContentPost.getText().toString();
//                if (content.isEmpty()) {
//                    Toast.makeText(PostWriterActivity.this, "Bài viết không có nội dung!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (title.isEmpty()) {
//                    Toast.makeText(PostWriterActivity.this, "Bài viết không có tiêu đề!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //block button post when clicked
//                ivPost.setClickable(false);
//
//                //get user
//                SQLiteHandler db = new SQLiteHandler(getApplicationContext());
//                HashMap<String, String> user = db.getUserDetails();
//                String uid = user.get("uid");
//
//                //get intent tu MainActivity
//                Intent intent = getIntent();
//                postPost(uid, idLop, keyLopType, title, content);
//
//            }
//        });
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
//                ivPost.setClickable(false);
                isAllowedClick = false;

                //get user
                SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                HashMap<String, String> user = db.getUserDetails();
                String uid = user.get("uid");

                //get intent tu MainActivity
//                Intent intent = getIntent();
                postPost(uid, idLop, keyLopType, title, content);
                break;
        }
        return true;
    }

    private void postPost(final String uid, final String group, final String base, final String title, final String content) {
        showDialog();
        Log.i(TAG, "uid " + uid);
        Log.i(TAG, "group " + group);
        Log.i(TAG, "base " + base);
        Log.i(TAG, "title " + title);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_POST_POST,
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

                                Message msg = new Message();
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
//                ivPost.setClickable(true);
                isAllowedClick = true;
                Log.i(TAG, "Post error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("author", uid);
                data.put("base", base);
                data.put("title", title);
                data.put("content", content);
                data.put("group", group);

                Log.i("content", content);

                return data;
            }
        };

        AppController.getInstance().addToRequestQueue(request, "post");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setResult(RESULT_OK);
            finish();
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
