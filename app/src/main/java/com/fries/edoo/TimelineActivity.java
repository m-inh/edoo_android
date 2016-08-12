package com.fries.edoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.adapter.TimeLineAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.models.ItemBase;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemLop;
import com.fries.edoo.models.ItemTimeLine;
import com.fries.edoo.utils.CommonVLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TooNies1810 on 8/12/16.
 */
public class TimelineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "TimelineActivity";
    private TimeLineAdapter mAdapter;
    private ArrayList<ItemBase> itemPostArr;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private ItemLop itemClass;

    private boolean isRefreshing;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //swipe refresh
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_timeline);
        swipeRefresh.setOnRefreshListener(this);

        Bundle b = this.getIntent().getExtras();
        itemClass = (ItemLop) b.getSerializable("item_class");

        toolbar.setTitle(itemClass.getTen());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Log.i(TAG, "id_class: " + itemClass.getIdData());

        initAdapter();
        initViews();
    }

    private void initAdapter() {
        itemPostArr = new ArrayList<>();
        mAdapter = new TimeLineAdapter(this, itemClass.getIdData(), "");

        requestPost(itemClass.getIdData());
//        onRefresh();
    }

    private LinearLayoutManager linearLayoutManager;

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //String title, String name, String ava, String content, int like, boolean isConfirmByTeacher
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_menu, menu);
        menu.findItem(R.id.item_post).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.item_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "id: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
//                Log.i(TAG, "id home: ");
                finish();
                break;
            case R.id.item_post:
                Log.i(TAG, "new post");
                startPostWriterActivity(itemClass.getIdData());
                break;
            case R.id.item_locbaidangchuatraloi:
                Log.i(TAG, "loc bai dang chua tl");
//                if (timelineFragment != null) {
//                    timelineFragment.locTheoBaiDang(TimeLineAdapter.BAI_DANG_LOC_THEO_CHUA_TRA_LOI);
//                }
                break;
            case R.id.item_locbaidanggiaovien:
                Log.i(TAG, "loc bai dang giao vien");
//                if (timelineFragment != null) {
//                    timelineFragment.locTheoBaiDang(TimeLineAdapter.BAI_DANG_LOC_THEO_GIAO_VIEN);
//                }
                break;
            case R.id.item_tatcabaidang:
                Log.i(TAG, "tat ca bai dang");
//                if (timelineFragment != null) {
//                    timelineFragment.locTheoBaiDang(TimeLineAdapter.BAI_DANG_BINH_THUONG);
//                }
                break;
            case R.id.action_sendAll:
//                Toast.makeText(this, "Send to all", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPost(final String id) {
        final ArrayList<ItemBase> itemPostArr = new ArrayList<>();

        String url = AppConfig.URL_GET_POST + "/" + id;

        RequestServer requestServer = new RequestServer(this, Request.Method.GET, url);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    Log.i(TAG, response.toString());

                    //lay jsonItem nhet vao item
                    JSONArray jsonPostArr = response.getJSONObject("data").getJSONArray("posts");

                    // create item timeline
                    for (int i = 0; i < jsonPostArr.length(); i++) {
                        //Lay mang cac post
                        //Luu vao 1 arrayList post
                        String id = jsonPostArr.getJSONObject(i).getString("id");
                        String titlePost = jsonPostArr.getJSONObject(i).getString("title");
                        String contentPost = jsonPostArr.getJSONObject(i).getString("content");
//                        int like = jsonPostArr.getJSONObject(i).getInt("vote");
                        int like = jsonPostArr.getJSONObject(i).getInt("vote_count");
                        int commentCount = jsonPostArr.getJSONObject(i).getInt("comment_count");
//                            boolean isConfirm = jsonPostArr.getJSONObject(i).getBoolean("confirm");
                        boolean isIncognito = jsonPostArr.getJSONObject(i).getInt("is_incognito") == 1;
                        String timeCreateAtPost = jsonPostArr.getJSONObject(i).getString("created_at");

                        //author post
                        String nameAuthorPost = "Incognito";
                        String idAuthorPost = "";
                        String emailAuthorPost = "";
                        String typeAuthorPost = "";
                        String mssvAuthorPost = "";
                        String avarAuthorPost = "okmen.com";

                        if (!isIncognito) {
                            JSONObject jsonAuthorPost = jsonPostArr.getJSONObject(i).getJSONObject("author");
                            nameAuthorPost = jsonAuthorPost.getString("name");
                            idAuthorPost = jsonAuthorPost.getString("id");
                            emailAuthorPost = jsonAuthorPost.getString("email");
                            typeAuthorPost = jsonAuthorPost.getString("capability");
                            mssvAuthorPost = jsonAuthorPost.getString("code");
                            avarAuthorPost = jsonAuthorPost.getString("avatar");
                        }

                        boolean isConfirm = false;
                        ItemTimeLine itemTimeLine = new ItemTimeLine(id, titlePost, nameAuthorPost, avarAuthorPost, contentPost, like, isConfirm);
                        itemTimeLine.setTypeAuthor(typeAuthorPost);
                        itemTimeLine.setCommentCount(commentCount);
//                        String inputDate = "2012-08-24T12:15:00+02:00";
                        String format = CommonVLs.TIME_FORMAT;

                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        try {
                            Date d = new Date(sdf.parse(timeCreateAtPost).getTime());
                            Log.i(TAG, "date create: " + d.getTime());
                            itemTimeLine.setCreateAt(d.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        itemPostArr.add(itemTimeLine);

                        //create comment array
                        //Lay mang cac comment
                        //Luu vao 1 arraylist comment
//                        JSONArray jsonCommentArr = jsonPostArr.getJSONObject(i).getJSONArray("comments");
//                        ArrayList<ItemComment> itemCommentArr = new ArrayList<>();
//                        for (int j = 0; j < jsonCommentArr.length(); j++) {
//
//                            String idComment = jsonCommentArr.getJSONObject(j).getString("id");
//                            String contentComment = jsonCommentArr.getJSONObject(j).getString("content");
//
//                            String idAuthorComment = "";
//                            String nameAuthorComment = "";
//                            String emailAuthorComment = "";
//                            String typeAuthorComment = "";
//                            String mssvAuthorComment = "";
//                            String avarAuthorComment = "";
//
//                            try {
//                                JSONObject jsonAuthorComment = jsonCommentArr.getJSONObject(j).getJSONObject("author");
//                                idAuthorComment = jsonAuthorComment.getString("id");
//                                nameAuthorComment = jsonAuthorComment.getString("name");
//                                emailAuthorComment = jsonAuthorComment.getString("email");
//                                typeAuthorComment = jsonAuthorComment.getString("type");
//                                mssvAuthorComment = jsonAuthorComment.getString("mssv");
//                                avarAuthorComment = jsonAuthorComment.getString("avatar");
//                            } catch (Exception e) {
//                                continue;
//                            }
//
//                            Log.i(TAG, "comment: " + nameAuthorComment);
//                            Log.i(TAG, "comment: " + emailAuthorComment);
//                            Log.i(TAG, "comment: " + typeAuthorComment);
//
//                            boolean isVote = jsonCommentArr.getJSONObject(j).getBoolean("confirmed");
//
//                            if (isVote || typeAuthorComment.equalsIgnoreCase("teacher")) {
//                                isConfirm = true;
//                                ((ItemTimeLine) itemPostArr.get(i)).setIsConfirmByTeacher(true);
//                            }
//
//                            itemCommentArr.add(new ItemComment(idComment, idAuthorComment, nameAuthorComment, avarAuthorComment, contentComment, isVote));
//
//                        }
//                        ((ItemTimeLine) itemPostArr.get(i)).setItemComments(itemCommentArr);
                    }
                    TimelineActivity.this.itemPostArr = itemPostArr;
                }
                Message msg = new Message();
                msg.setTarget(mHandler);
                msg.sendToTarget();
            }
        });

        requestServer.sendRequest("get posts");
    }

    private static final int REQUEST_CODE_POST_DETAIL = 1201;
    private static final int REQUEST_CODE_POST_WRITER = 1202;

    public void startPostDetailActivity(ItemTimeLine itemTimeLine) {
        Intent mIntent = new Intent();
        mIntent.putExtra("timelineItem", itemTimeLine);
        mIntent.setClass(this, PostDetailActivity.class);
        startActivityForResult(mIntent, REQUEST_CODE_POST_DETAIL);
    }

    public void startPostWriterActivity(String idClass) {
        Intent intent = new Intent();
        intent.putExtra("class_id", idClass);
        intent.setClass(TimelineActivity.this, PostWriterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_POST_WRITER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "ok men");
        Log.i(TAG, "code: " + requestCode);
        Log.i(TAG, "result code: " + resultCode);
        if (requestCode == REQUEST_CODE_POST_DETAIL){
            if (resultCode == RESULT_OK){
                ItemTimeLine itemTimeLine = (ItemTimeLine) data.getExtras().getSerializable("item_timeline");

                Log.i(TAG, "like: " + itemTimeLine.getLike());
                Log.i(TAG, "cmt: " + itemTimeLine.getCommentCount());

                String idPost = itemTimeLine.getIdPost();

                for (int i = 0; i <itemPostArr.size(); i++) {
                    ItemTimeLine tempItem = (ItemTimeLine) itemPostArr.get(i);
                    if (idPost.equalsIgnoreCase(tempItem.getIdPost())){
                        Log.i(TAG, "ok men" + idPost);
                        Log.i(TAG, "like " + tempItem.getLike());
                        Log.i(TAG, "cmt: " + tempItem.getCommentCount());

                        tempItem.setLike(itemTimeLine.getLike());
                        tempItem.setCommentCount(itemTimeLine.getCommentCount());
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            mAdapter.updateList(itemPostArr, linearLayoutManager.findLastVisibleItemPosition());
        }
    };

    @Override
    public void onRefresh() {
        requestPost(itemClass.getIdData());
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setIsRefreshing(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
    }

    public void locTheoBaiDang(int mode) {
        mAdapter.locBaiDang(mode);
    }

}
