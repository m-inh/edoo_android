package com.uet.fries.edoo.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.fragment.PostWriterContentFragment;
import com.uet.fries.edoo.fragment.PostWriterTagFragment;
import com.uet.fries.edoo.models.ItemTimeLine;
import com.uet.fries.edoo.utils.Reporter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tmq on 20/08/2016.
 */
public class PostWriterActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = PostWriterActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private PostAdapter postAdapter;

    private ProgressDialog pDialog;
    private boolean isModeWritePost = true;
    private ItemTimeLine itemTimeLine = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Reporter.register(this);// Crash Reporter

        setContentView(com.uet.fries.edoo.R.layout.activity_post_writer_view_pager);
        toolbar = (Toolbar) findViewById(R.id.tb_post_writer);
        setSupportActionBar(toolbar);

        itemTimeLine = (ItemTimeLine) getIntent().getSerializableExtra("timelineItem");
        if (itemTimeLine != null) {
            toolbar.setTitle(R.string.txt_edit_post);
            isModeWritePost = false;
        }

        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(com.uet.fries.edoo.R.id.tb_post_writer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(com.uet.fries.edoo.R.id.tl_post_writer);
        viewPager = (ViewPager) findViewById(com.uet.fries.edoo.R.id.vp_post_writer);

        postAdapter = new PostAdapter(getSupportFragmentManager());
        viewPager.setAdapter(postAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(this);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                showActionNexPage(true);
                break;
            case 1:
                if (!postAdapter.getPostWriterContent().checkFillContent()) {
                    viewPager.setCurrentItem(0, true);
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    break;
                } else {
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                }
                showActionNexPage(false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
    // ---------------------------------------------------------------------------------------------

    private void postToServer() {
//        postAdapter.getPostWriterContent().replaceUrlImage();
        String titlePost = postAdapter.getPostWriterContent().getTitlePost();
        String contentPost = postAdapter.getPostWriterContent().getContentPost();

        if (!postAdapter.getPostWriterContent().checkFillContent()) {
            viewPager.setCurrentItem(0, true);
            return;
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.show();

        Intent mIntent = getIntent();
        String idLop = mIntent.getStringExtra("class_id");

        PostWriterTagFragment pTag = postAdapter.getPostWriterTagFragment();

        if (isModeWritePost)
            postPost(idLop, titlePost, contentPost, pTag.getTypePost(), pTag.getIsIncognitoPost(), pTag.getIsTeacher(), pTag.getTimestamp());
        else
            updatePost(itemTimeLine.getIdPost(), titlePost, contentPost, pTag.getIsIncognitoPost(), pTag.getTypePost());
    }

    private MenuItem actionNextPage, actionPost;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.uet.fries.edoo.R.menu.writepost_menu, menu);
        actionNextPage = menu.findItem(com.uet.fries.edoo.R.id.action_next_page_write_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        actionPost = menu.findItem(com.uet.fries.edoo.R.id.action_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        showActionNexPage(true);
        return true;
    }

    private void showActionNexPage(boolean isShow) {
        actionPost.setVisible(!isShow);
        actionNextPage.setVisible(isShow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        exitWriterPost();
                        break;
                    case 1:
                        viewPager.setCurrentItem(0, true);
                }
                break;
            case com.uet.fries.edoo.R.id.action_next_page_write_post:
                if (postAdapter.getPostWriterContent().checkFillContent()) {
                    viewPager.setCurrentItem(1, true);
                }
//                Log.i(TAG, postAdapter.getPostWriterContent().getContentPost());
                break;
            case com.uet.fries.edoo.R.id.action_post:
                postToServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) viewPager.setCurrentItem(0, true);
        else exitWriterPost();
    }

    public void exitWriterPost() {
        PostWriterContentFragment writer = postAdapter.getPostWriterContent();
        String titlePost = writer.getTitlePost();
        if (isModeWritePost && (!writer.contentIsEmpty() || !titlePost.isEmpty())) {
            AlertDialog.Builder notiBack = new AlertDialog.Builder(this)
//                    .setTitle("Xóa bài viết?")
                    .setMessage(R.string.warn_exit_and_delete)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PostWriterActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Không", null);
            notiBack.show();
        } else if (!isModeWritePost && !writer.contentIsNotChanged()) {
            AlertDialog.Builder notiBack = new AlertDialog.Builder(this)
//                    .setTitle("Thoát?")
                    .setMessage(R.string.warn_exit_edit)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PostWriterActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Không", null);
            notiBack.show();
        } else super.onBackPressed();
    }

    // --------------------------------- Request Server --------------------------------------------
    public void postPost(String classId, String title, String content, String type, boolean isIncognito, boolean isPostTeacher, String timestamp) {
        String url = AppConfig.URL_POST_POST;

        JSONObject params = new JSONObject();
        try {
            params.put("class_id", classId);
            params.put("title", title);
            params.put("content", content);
            params.put("type", type);
            params.put("is_incognito", isIncognito);
            if (!timestamp.equals("0")) params.put("event_end", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(this, Request.Method.POST, url, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                pDialog.dismiss();
                if (!error) {
//                    Log.i(TAG, response.toString());

                    Message msg = new Message();
                    msg.setTarget(mHandler);
                    msg.sendToTarget();

                } else {
//                    ivPost.setClickable(true);
//                    isAllowedClick = true;
//                    Log.i(TAG, "Post error: " + message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });

        requestServer.sendRequest("post new post");
    }

    private void updatePost(String postId, String title, String content, boolean isIncognito, String type) {
        JSONObject params = new JSONObject();
        try {
            params.put("post_id", postId);
            params.put("title", title);
            params.put("content", content);
            params.put("is_incognito", isIncognito);
            params.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer request = new RequestServer(this, Request.Method.POST, AppConfig.URL_EDIT_POST, params);
        request.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                pDialog.dismiss();
                if (!error) {
                    Toast.makeText(getApplicationContext(), "Đã chỉnh sửa bài viết", Toast.LENGTH_SHORT).show();
                    Message msg = new Message();
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                } else {
                    Log.d(TAG, "Error update post: " + response.toString());
                }
            }
        });
        request.sendRequest("update_post");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setResult(RESULT_OK);
            finish();
        }
    };


    // ----------------------------------------- Adapter -------------------------------------------
    private class PostAdapter extends FragmentStatePagerAdapter {
        private PostWriterContentFragment postWriterContent;
        private PostWriterTagFragment postWriterTagFragment;

        public PostAdapter(FragmentManager fm) {
            super(fm);
            postWriterContent = PostWriterContentFragment.newInstance(
                    isModeWritePost ? "" : itemTimeLine.getTitle(), isModeWritePost ? "" : itemTimeLine.getContent());
            postWriterTagFragment = PostWriterTagFragment.newInstance(
                    isModeWritePost ? "" : itemTimeLine.getType(), !isModeWritePost && itemTimeLine.isIncognito());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return postWriterContent;
                default:
                    return postWriterTagFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public PostWriterContentFragment getPostWriterContent() {
            return postWriterContent;
        }

        public PostWriterTagFragment getPostWriterTagFragment() {
            return postWriterTagFragment;
        }
    }
}
