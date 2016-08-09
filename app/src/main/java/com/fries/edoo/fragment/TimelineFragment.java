package com.fries.edoo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.R;
import com.fries.edoo.adapter.TimeLineAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.models.ItemBase;
import com.fries.edoo.models.ItemComment;
import com.fries.edoo.models.ItemTimeLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public class TimelineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TimeLineAdapter.OnBindItemComplete {
    private static final String TAG = "TimelineFragment";
    private View root;
    private Context mainContext;

    private TimeLineAdapter mAdapter;
    private ArrayList<ItemBase> itemPostArr;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private String idLop;
    private String keyLopType;

    private boolean isRefreshing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_timeline, null);
        mainContext = getActivity();

        //swipe refresh
        swipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.swipe_timeline);
        swipeRefresh.setOnRefreshListener(this);

        Bundle b = this.getArguments();
        keyLopType = b.getString("keyLop");
        idLop = b.getString("idData");

        Log.i(TAG, "keyLopType" + keyLopType + "");
        Log.i(TAG, "idLop" + idLop);

        initAdapter();
        initViews();

        return root;
    }

    private void initAdapter() {
        itemPostArr = new ArrayList<>();
        mAdapter = new TimeLineAdapter(mainContext, idLop, keyLopType, this);

        requestPost(idLop, keyLopType);
//        onRefresh();
    }

    private LinearLayoutManager linearLayoutManager;

    private void initViews() {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(mainContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //String title, String name, String ava, String content, int like, boolean isConfirmByTeacher
        mRecyclerView.setAdapter(mAdapter);
    }

    //    private void requestPost(final String id, final String database_type, ArrayList<ItemBase> itemPostArr) {
    private void requestPost(final String id, final String database_type) {
        //Hien thi 1 dialog cho request
        isRefreshing = true;
        swipeRefresh.setRefreshing(true);

//        itemPostArr.clear();
        final ArrayList<ItemBase> itemPostArr = new ArrayList<>();
//        itemPostArr.add(new ItemBase());

        StringRequest request = new StringRequest(Method.POST, AppConfig.URL_GET_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.i(TAG, "nhay vao onResponse");
                Log.i(TAG, response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        //lay jsonItem nhet vao item
                        JSONArray jsonPostArr = jsonObject.getJSONArray("posts");

                        // create item timeline
                        for (int i = 0; i < jsonPostArr.length(); i++) {
                            //Lay mang cac post
                            //Luu vao 1 arrayList post
                            String id = jsonPostArr.getJSONObject(i).getString("id");
                            String titlePost = jsonPostArr.getJSONObject(i).getString("title");
                            String contentPost = jsonPostArr.getJSONObject(i).getString("content");
                            String groupType = jsonPostArr.getJSONObject(i).getString("group");
                            int like = jsonPostArr.getJSONObject(i).getInt("like");
//                            boolean isConfirm = jsonPostArr.getJSONObject(i).getBoolean("confirm");
                            boolean isIncognito = jsonPostArr.getJSONObject(i).getBoolean("isIncognito");
                            String basePost = jsonPostArr.getJSONObject(i).getString("base");
                            String timeCreateAtPost = jsonPostArr.getJSONObject(i).getString("created_at");

                            //author post
                            JSONObject jsonAuthorPost = jsonPostArr.getJSONObject(i).getJSONObject("author");
                            String nameAuthorPost = jsonAuthorPost.getString("name");
                            String idAuthorPost = jsonAuthorPost.getString("id");
                            String emailAuthorPost = jsonAuthorPost.getString("email");
                            String typeAuthorPost = jsonAuthorPost.getString("type");
                            String mssvAuthorPost = jsonAuthorPost.getString("mssv");
                            String avarAuthorPost = jsonAuthorPost.getString("avatar");

                            boolean isConfirm = false;
//                            if (typeAuthorPost.equalsIgnoreCase("teacher")){
//                                isConfirm = true;
//                            }
                            ItemTimeLine itemTimeLine = new ItemTimeLine(id, titlePost, nameAuthorPost, avarAuthorPost, contentPost, like, isConfirm);
                            itemTimeLine.setTypeAuthor(typeAuthorPost);
                            itemTimeLine.setCreateAt(timeCreateAtPost);
                            itemPostArr.add(itemTimeLine);

                            //create comment array
                            //Lay mang cac comment
                            //Luu vao 1 arraylist comment
                            JSONArray jsonCommentArr = jsonPostArr.getJSONObject(i).getJSONArray("comments");
                            ArrayList<ItemComment> itemCommentArr = new ArrayList<>();
                            for (int j = 0; j < jsonCommentArr.length(); j++) {

                                String idComment = jsonCommentArr.getJSONObject(j).getString("id");
                                String contentComment = jsonCommentArr.getJSONObject(j).getString("content");

                                String idAuthorComment = "";
                                String nameAuthorComment = "";
                                String emailAuthorComment = "";
                                String typeAuthorComment = "";
                                String mssvAuthorComment = "";
                                String avarAuthorComment = "";

                                try{
                                    JSONObject jsonAuthorComment = jsonCommentArr.getJSONObject(j).getJSONObject("author");
                                    idAuthorComment = jsonAuthorComment.getString("id");
                                    nameAuthorComment = jsonAuthorComment.getString("name");
                                    emailAuthorComment = jsonAuthorComment.getString("email");
                                    typeAuthorComment = jsonAuthorComment.getString("type");
                                    mssvAuthorComment = jsonAuthorComment.getString("mssv");
                                    avarAuthorComment = jsonAuthorComment.getString("avatar");
                                } catch (Exception e){
                                    continue;
                                }

                                Log.i(TAG, "comment: " + nameAuthorComment);
                                Log.i(TAG, "comment: " + emailAuthorComment);
                                Log.i(TAG, "comment: " + typeAuthorComment);

                                boolean isVote = jsonCommentArr.getJSONObject(j).getBoolean("confirmed");

                                if (isVote || typeAuthorComment.equalsIgnoreCase("teacher")) {
                                    isConfirm = true;
                                    ((ItemTimeLine) itemPostArr.get(i)).setIsConfirmByTeacher(true);
                                }

                                itemCommentArr.add(new ItemComment(idComment, idAuthorComment, nameAuthorComment, avarAuthorComment, contentComment, isVote));

                            }
                            ((ItemTimeLine) itemPostArr.get(i)).setItemComments(itemCommentArr);
                            ((ItemTimeLine) itemPostArr.get(i)).setKeyLopType(keyLopType);
                        }
                        TimelineFragment.this.itemPostArr = itemPostArr;
                        Message msg = new Message();
                        msg.setTarget(mHandler);
                        msg.sendToTarget();
                    } else {
                        isRefreshing = false;
                        if (swipeRefresh.isRefreshing()) {
                            swipeRefresh.setRefreshing(false);
                        }
                    }
                } catch (JSONException e) {
                    isRefreshing = false;
                    e.printStackTrace();
                    if (swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(false);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRefreshing = false;
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                Toast.makeText(mainContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> lop = new HashMap<>();
                lop.put("id", id);
                lop.put("base", database_type);
                return lop;
            }
        };
        AppController.getInstance().addToRequestQueue(request, "timeline_item");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (swipeRefresh.isRefreshing()){
                swipeRefresh.setRefreshing(false);
            }
//            ArrayList<ItemTimeLine> itemTimeLines = new ArrayList<>();
//
//            for (int i = 0; i < itemPostArr.size(); i++) {
//                if (i != 0){
//                    itemTimeLines.add((ItemTimeLine)itemPostArr.get(i));
//                }
//            }

            mAdapter.updateList(itemPostArr, linearLayoutManager.findLastVisibleItemPosition());
//            isRefreshing = false;

//            Log.i(TAG, linearLayoutManager.findLastCompletelyVisibleItemPosition() + " findLastCompletelyVisibleItemPosition");
//            Log.i(TAG, linearLayoutManager.findFirstCompletelyVisibleItemPosition() + " findFirstCompletelyVisibleItemPosition");
//            Log.i(TAG, linearLayoutManager.findLastVisibleItemPosition() + " findLastVisibleItemPosition");
//            Log.i(TAG, linearLayoutManager.findFirstVisibleItemPosition() + " findFirstVisibleItemPosition");
        }
    };

    @Override
    public void onRefresh() {
//        Log.i(TAG, "onRefesh");

//        requestPost(idLop, keyLopType, itemPostArr);
        requestPost(idLop, keyLopType);
    }

//    @Override
//    public void onDestroy() {
//        Log.i(TAG, "onDestroy");
////        swipeRefresh.setRefreshing(false);
////        swipeRefresh.setEnabled(false);
//        super.onDestroy();
//    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setIsRefreshing(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
    }

    public void locTheoBaiDang(int mode) {
        mAdapter.locBaiDang(mode);
    }

    public String getIdLop() {
        return idLop;
    }

    public String getKeyLopType() {
        return keyLopType;
    }
}
