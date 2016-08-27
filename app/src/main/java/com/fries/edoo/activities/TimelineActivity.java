package com.fries.edoo.activities;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

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
public class TimelineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "TimelineActivity";
    private TimeLineAdapter mAdapter;
    private ArrayList<ItemBase> itemPostArr;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private ItemLop itemClass;

    private Toolbar toolbar;
    private int currPage = 1;

    public static final int BAI_DANG_BINH_THUONG = 1;
    public static final int BAI_DANG_LOC_THEO_GIAO_VIEN = 2;
    public static final int BAI_DANG_LOC_THEO_CHUA_TRA_LOI = 3;
    public static final int BAI_DANG_CHUA_DOC = 4;

    private int currTypeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_timeline);
        swipeRefresh.setOnRefreshListener(this);

        Bundle b = this.getIntent().getExtras();
        itemClass = (ItemLop) b.getSerializable("item_class");

        toolbar.setTitle(itemClass.getTen());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currTypeFilter = 1;

        initAdapter();
        initViews();
    }

    private void initAdapter() {
        itemPostArr = new ArrayList<>();
        mAdapter = new TimeLineAdapter(this, itemClass.getIdData());

        requestPost(itemClass.getIdData(), currPage, currTypeFilter);
    }

    private LinearLayoutManager linearLayoutManager;
    private int visibleThreshold = 0;
    private boolean isLoading;
    private boolean isLoadable = true;

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount() - 1;
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.i(TAG, "total: " + totalItemCount);
//                Log.i(TAG, "latest: " + lastVisibleItem);

                if (isLoadable && !isLoading && totalItemCount == (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    isLoading = true;
                }
            }
        });
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_post:
                Log.i(TAG, "new post");
                startPostWriterActivity(itemClass.getIdData());
                break;

            // filter
            case R.id.item_tatcabaidang:
                Log.i(TAG, "tat ca bai dang");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_BINH_THUONG);
                currTypeFilter = BAI_DANG_BINH_THUONG;
                break;
            case R.id.item_locbaidangchuatraloi:
                Log.i(TAG, "loc bai dang chua tl");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_LOC_THEO_CHUA_TRA_LOI);
                currTypeFilter = BAI_DANG_LOC_THEO_CHUA_TRA_LOI;
                break;
            case R.id.item_locbaidanggiaovien:
                Log.i(TAG, "loc bai dang giao vien");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_LOC_THEO_GIAO_VIEN);
                currTypeFilter = BAI_DANG_LOC_THEO_GIAO_VIEN;
                break;
            case R.id.item_locbaidangchuadoc:
                Log.i(TAG, "tat ca bai dang");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_CHUA_DOC);
                currTypeFilter = BAI_DANG_CHUA_DOC;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMore() {
        requestPost(itemClass.getIdData(), ++currPage, currTypeFilter);
    }

    private void requestPost(final String classId, int pageNumber, int typeFilter) {
        final ArrayList<ItemBase> itemPostArr = new ArrayList<>();

        String queryParams = "";

        switch (typeFilter) {
            case BAI_DANG_BINH_THUONG:
                break;
            case BAI_DANG_LOC_THEO_GIAO_VIEN:
                queryParams += "?filter=post_teacher";
                break;
            case BAI_DANG_LOC_THEO_CHUA_TRA_LOI:
                queryParams += "?filter=post_notsolve";
                break;
            case BAI_DANG_CHUA_DOC:
                queryParams += "?filter=post_notseen";
                break;
        }

        String url = AppConfig.URL_GET_POST_IN_PAGE + "/" + classId + "/page" + "/" + pageNumber + queryParams;

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
                        String desPost = jsonPostArr.getJSONObject(i).getString("description");
                        int like = jsonPostArr.getJSONObject(i).getInt("vote_count");
                        int commentCount = jsonPostArr.getJSONObject(i).getInt("comment_count");
                        boolean isIncognito = jsonPostArr.getJSONObject(i).getInt("is_incognito") == 1;
                        boolean isSeen = jsonPostArr.getJSONObject(i).getInt("is_seen") == 1;
                        boolean isSolve = jsonPostArr.getJSONObject(i).getInt("is_solve") == 1;
                        String timeCreateAtPost = jsonPostArr.getJSONObject(i).getString("created_at");
                        String type = jsonPostArr.getJSONObject(i).getString("type");

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
                        ItemTimeLine itemTimeLine = new ItemTimeLine(id, titlePost, nameAuthorPost, avarAuthorPost, contentPost, like, isConfirm, type);
                        itemTimeLine.setTypeAuthor(typeAuthorPost);
                        itemTimeLine.setDescription(desPost);
                        itemTimeLine.setIdAuthor(idAuthorPost);
                        itemTimeLine.setCommentCount(commentCount);
                        itemTimeLine.setIsSeen(isSeen);
                        itemTimeLine.setSolve(isSolve);
//                        String inputDate = "2012-08-24T12:15:00+02:00";
                        String format = CommonVLs.TIME_FORMAT;

                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        try {
                            Date d = new Date(sdf.parse(timeCreateAtPost).getTime());
//                            Log.i(TAG, "date create: " + d.getTime());
                            itemTimeLine.setCreateAt(d.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        itemPostArr.add(itemTimeLine);
                    }

                    JSONObject jsonPage = response.getJSONObject("data").getJSONObject("pagination");
                    int curPage = jsonPage.getInt("page");
                    int pageCount = jsonPage.getInt("pageCount");

                    Log.i(TAG, "curPage json: " + curPage);
                    Log.i(TAG, "page count json: " + pageCount);

                    isLoadable = curPage < pageCount;
                    TimelineActivity.this.itemPostArr = itemPostArr;
                    Message msg = new Message();
                    msg.what = SUCCESS;
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                } else {
                    isLoadable = false;
                    Message msg = new Message();
                    msg.what = FAIL;
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                }
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

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
//                    R.anim.anim_enter_activity, R.anim.anim_exit_activity);
//            startActivityForResult(mIntent, REQUEST_CODE_POST_DETAIL, optionsCompat.toBundle());
//        } else
        startActivityForResult(mIntent, REQUEST_CODE_POST_DETAIL);
    }

    public void startPostWriterActivity(String idClass) {
        Intent mIntent = new Intent();
        mIntent.putExtra("class_id", idClass);
        mIntent.setClass(TimelineActivity.this, PostWriterActivity.class);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
//                    R.anim.anim_enter_activity, R.anim.anim_exit_activity);
//            startActivityForResult(mIntent, REQUEST_CODE_POST_WRITER, optionsCompat.toBundle());
//        } else
        startActivityForResult(mIntent, REQUEST_CODE_POST_WRITER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_POST_DETAIL) {
            if (resultCode == RESULT_OK) {
                ItemTimeLine itemTimeLine = (ItemTimeLine) data.getExtras().getSerializable("item_timeline");

                String idPost = itemTimeLine.getIdPost();

                for (int i = 0; i < mAdapter.getItemArr().size() - 1; i++) {
                    ItemTimeLine tempItem = (ItemTimeLine) mAdapter.getItemArr().get(i);
                    if (idPost.equalsIgnoreCase(tempItem.getIdPost())) {
                        tempItem.setLike(itemTimeLine.getLike());
                        tempItem.setCommentCount(itemTimeLine.getCommentCount());
                        tempItem.setSolve(itemTimeLine.isSolve());
                    }
                }
            }
        }

        if (requestCode == REQUEST_CODE_POST_WRITER) {
            if (resultCode == RESULT_OK) {
                refreshPosts();
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    public static int SUCCESS = 1;
    public static int FAIL = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            Log.i(TAG, "current page: " + currPage);

            mAdapter.setLoadable(isLoadable);
            if (msg.what == SUCCESS) {
                if (currPage == 1) {
                    mAdapter.updateList(itemPostArr);
                } else {
                    mAdapter.addItems(itemPostArr);
                    Log.i(TAG, "add items");
                }
            }

            isLoading = false;
        }
    };

    @Override
    public void onRefresh() {
        refreshPosts();
    }

    private void refreshPosts() {
        currPage = 1;
        requestPost(itemClass.getIdData(), currPage, currTypeFilter);
    }

}
