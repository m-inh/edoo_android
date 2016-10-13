package com.uet.fries.edoo.activities;

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
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;

import com.uet.fries.edoo.adapter.TimeLineAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.models.ITimelineBase;
import com.uet.fries.edoo.models.ItemLop;
import com.uet.fries.edoo.models.ItemTimeLineExercise;
import com.uet.fries.edoo.models.ItemTimeLinePost;
import com.uet.fries.edoo.utils.Reporter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TooNies1810 on 8/12/16.
 */
public class TimelineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = TimelineActivity.class.getSimpleName();
    private TimeLineAdapter mAdapter;
    private ArrayList<ITimelineBase> itemPostArr;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar pbLoading;

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

        Reporter.register(this);// Crash Reporter

        setContentView(com.uet.fries.edoo.R.layout.activity_timeline);


        toolbar = (Toolbar) findViewById(com.uet.fries.edoo.R.id.toolbar);

        swipeRefresh = (SwipeRefreshLayout) findViewById(com.uet.fries.edoo.R.id.swipe_timeline);
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
        pbLoading = (ProgressBar) findViewById(com.uet.fries.edoo.R.id.pb_loading_timeline);
        mRecyclerView = (RecyclerView) findViewById(com.uet.fries.edoo.R.id.rv_main);
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
        getMenuInflater().inflate(com.uet.fries.edoo.R.menu.timeline_menu, menu);
        menu.findItem(com.uet.fries.edoo.R.id.item_post).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(com.uet.fries.edoo.R.id.item_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case com.uet.fries.edoo.R.id.item_post:
//                Log.i(TAG, "new post");
                startPostWriterActivity(itemClass.getIdData());
                break;

            // filter
            case com.uet.fries.edoo.R.id.item_tatcabaidang:
//                Log.i(TAG, "tat ca bai dang");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_BINH_THUONG);
                currTypeFilter = BAI_DANG_BINH_THUONG;
                break;
            case com.uet.fries.edoo.R.id.item_locbaidangchuatraloi:
//                Log.i(TAG, "loc bai dang chua tl");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_LOC_THEO_CHUA_TRA_LOI);
                currTypeFilter = BAI_DANG_LOC_THEO_CHUA_TRA_LOI;
                break;
            case com.uet.fries.edoo.R.id.item_locbaidanggiaovien:
//                Log.i(TAG, "loc bai dang giao vien");
                currPage = 1;
                requestPost(itemClass.getIdData(), currPage, BAI_DANG_LOC_THEO_GIAO_VIEN);
                currTypeFilter = BAI_DANG_LOC_THEO_GIAO_VIEN;
                break;
            case com.uet.fries.edoo.R.id.item_locbaidangchuadoc:
//                Log.i(TAG, "tat ca bai dang");
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
        final ArrayList<ITimelineBase> itemPostArr = new ArrayList<>();

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
                    pbLoading.setVisibility(View.GONE);
//                    Log.i(TAG, response.toString());

                    //lay jsonItem nhet vao item
                    JSONArray jsonPostArr = response.getJSONObject("data").getJSONArray("posts");

                    // create item timeline
                    for (int i = 0; i < jsonPostArr.length(); i++) {
                        try {
                            ITimelineBase itemTimeline =
                                    jsonPostArr.getJSONObject(i).getString("type").equals(ITimelineBase.TYPE_POST_EXERCISE) ?
                                            new ItemTimeLineExercise(jsonPostArr.getJSONObject(i)) :
                                            new ItemTimeLinePost(jsonPostArr.getJSONObject(i));

                            itemPostArr.add(itemTimeline);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Parse json to ItemTimeLine is Failed!");
                        }
                    }

                    JSONObject jsonPage = response.getJSONObject("data").getJSONObject("pagination");
                    int curPage = jsonPage.getInt("page");
                    int pageCount = jsonPage.getInt("pageCount");

//                    Log.i(TAG, "curPage json: " + curPage);
//                    Log.i(TAG, "page count json: " + pageCount);

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

        if (!requestServer.sendRequest("get posts")) {
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }
        }
    }

    private static final int REQUEST_CODE_POST_DETAIL = 1201;
    private static final int REQUEST_CODE_POST_WRITER = 1202;

    public void startPostDetailActivity(ITimelineBase itemTimeLine) {
        Intent mIntent = new Intent();
        mIntent.putExtra("timelineItem", itemTimeLine);
        mIntent.putExtra("post_id", itemTimeLine.getIdPost());
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
                ITimelineBase itemTimeLine = (ITimelineBase) data.getExtras().getSerializable("item_timeline");

                if (itemTimeLine==null) return;

                String idPost = itemTimeLine.getIdPost();

                for (int i = 0; i < mAdapter.getItemArr().size() - 1; i++) {
                    ITimelineBase tempItem =  mAdapter.getItemArr().get(i);
                    if (idPost.equalsIgnoreCase(tempItem.getIdPost())) {
                        mAdapter.getItemArr().set(i, itemTimeLine);
                    }
                }
            } else if (resultCode == PostDetailActivity.RESULT_DELETE_COMPLETE) {
                refreshPosts();
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

//            Log.i(TAG, "current page: " + currPage);

            mAdapter.setLoadable(isLoadable);
            if (msg.what == SUCCESS) {
                if (currPage == 1) {
                    mAdapter.updateList(itemPostArr);
                } else {
                    mAdapter.addItems(itemPostArr);
//                    Log.i(TAG, "add items");
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
