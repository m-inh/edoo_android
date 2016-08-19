package com.fries.edoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.fries.edoo.R;
import com.fries.edoo.adapter.TimeLineAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.models.ItemBase;
import com.fries.edoo.models.ItemLop;
import com.fries.edoo.models.ItemTimeLine;
import com.fries.edoo.utils.CommonVLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        initAdapter();
        initViews();
    }

    private void initAdapter() {
        itemPostArr = new ArrayList<>();
        mAdapter = new TimeLineAdapter(this, itemClass.getIdData());

        requestPost(itemClass.getIdData());
    }

    private LinearLayoutManager linearLayoutManager;

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
                finish();
                break;
            case R.id.item_post:
                Log.i(TAG, "new post");
                startPostWriterActivity(itemClass.getIdData());
                break;
            case R.id.item_locbaidangchuatraloi:
                Log.i(TAG, "loc bai dang chua tl");
                mAdapter.locBaiDang(TimeLineAdapter.BAI_DANG_LOC_THEO_CHUA_TRA_LOI);
                break;
            case R.id.item_locbaidanggiaovien:
                Log.i(TAG, "loc bai dang giao vien");
                mAdapter.locBaiDang(TimeLineAdapter.BAI_DANG_LOC_THEO_GIAO_VIEN);
                break;
            case R.id.item_tatcabaidang:
                Log.i(TAG, "tat ca bai dang");
                mAdapter.locBaiDang(TimeLineAdapter.BAI_DANG_BINH_THUONG);
                break;
            case R.id.item_locbaidangchuadoc:
                Log.i(TAG, "tat ca bai dang");
                mAdapter.locBaiDang(TimeLineAdapter.BAI_DANG_CHUA_DOC);
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
                        int like = jsonPostArr.getJSONObject(i).getInt("vote_count");
                        int commentCount = jsonPostArr.getJSONObject(i).getInt("comment_count");
                        boolean isIncognito = jsonPostArr.getJSONObject(i).getInt("is_incognito") == 1;
                        boolean isSeen = jsonPostArr.getJSONObject(i).getInt("is_seen") == 1;
                        boolean isSolve = jsonPostArr.getJSONObject(i).getInt("is_solve") == 1;
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
                        itemTimeLine.setIdAuthor(idAuthorPost);
                        itemTimeLine.setCommentCount(commentCount);
                        itemTimeLine.setIsSeen(isSeen);
                        itemTimeLine.setSolve(isSolve);
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
        if (requestCode == REQUEST_CODE_POST_DETAIL){
            if (resultCode == RESULT_OK){
                ItemTimeLine itemTimeLine = (ItemTimeLine) data.getExtras().getSerializable("item_timeline");

                String idPost = itemTimeLine.getIdPost();

                for (int i = 0; i <itemPostArr.size(); i++) {
                    ItemTimeLine tempItem = (ItemTimeLine) itemPostArr.get(i);
                    if (idPost.equalsIgnoreCase(tempItem.getIdPost())){
                        tempItem.setLike(itemTimeLine.getLike());
                        tempItem.setCommentCount(itemTimeLine.getCommentCount());
                        tempItem.setSolve(itemTimeLine.isSolve());
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == REQUEST_CODE_POST_WRITER){
            if (resultCode == RESULT_OK){
                // refresh data
                requestPost(itemClass.getIdData());
            }
        }

        mAdapter.refreshList();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }
            mAdapter.updateList(itemPostArr);
        }
    };

    @Override
    public void onRefresh() {
        requestPost(itemClass.getIdData());
    }
}
