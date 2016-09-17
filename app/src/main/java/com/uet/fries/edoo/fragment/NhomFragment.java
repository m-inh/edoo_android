package com.uet.fries.edoo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.uet.fries.edoo.activities.MainActivity;
import com.uet.fries.edoo.adapter.LopAdapter;
import com.uet.fries.edoo.app.AppManager;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.models.ItemLop;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TooNies1810 on 1/11/16.
 */
public class NhomFragment extends LopFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener{
    private ArrayList<ItemLop> itemLopArr;

    private boolean firstLoading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!firstLoading){
            return root;
        }
        root = inflater.inflate(com.uet.fries.edoo.R.layout.fragment_nhom, null);
        mContext = getActivity();
        AppManager.getInstance().setMainContext(mContext);

        // get user information
        db = new SQLiteHandler(mContext);
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        //Swipe refresh
        swipeRefresh = (SwipeRefreshLayout) root.findViewById(com.uet.fries.edoo.R.id.swipe);
        swipeRefresh.setOnRefreshListener(this);

        initAdapter();
        initViews();

        firstLoading = false;
        return root;
    }

    private void initAdapter() {
        itemLopArr = new ArrayList<>();
        mAdapter = new LopAdapter(mContext);

        // Get du lieu lop mon hoc tu server
        requestLopHoc(uid, LopFragment.KEY_NHOM, itemLopArr);
    }

    private void initViews() {
        lvMain = (ListView) root.findViewById(com.uet.fries.edoo.R.id.lv_lopmonhoc);
        lvMain.setOnItemClickListener(this);
        lvMain.setAdapter(mAdapter);
    }

    @Override
    protected void onFail() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    protected void onPostComplete() {
        mAdapter.setItemArr(itemLopArr);

        if (swipeRefresh.isRefreshing()){
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        Toast.makeText(mContext, "on refresh", Toast.LENGTH_SHORT).show();

        // Get du lieu lop mon hoc tu server
        requestLopHoc(uid, LopFragment.KEY_NHOM, itemLopArr);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();

        if (!swipeRefresh.isRefreshing()){
            MainActivity mainActivity = (MainActivity) mContext;
            mainActivity.goToTimeLine(itemLopArr.get(position), LopFragment.KEY_NHOM);
        }
    }
}