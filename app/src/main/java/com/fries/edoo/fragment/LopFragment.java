package com.fries.edoo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.adapter.LopAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemLop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public abstract class LopFragment extends Fragment {
    private static final String TAG = "LopFragment";
    protected View root;
    protected ListView lvMain;
    protected LopAdapter mAdapter;
    protected Context mContext;

    protected SwipeRefreshLayout swipeRefresh;

    public static final String KEY_LOP_MON_HOC = "classSubject";
    public static final String KEY_LOP_KHOA_HOC = "class_xes";
    public static final String KEY_NHOM = "nhom";

    protected SQLiteHandler db;
    protected String uid;

    private boolean isRefreshing;

    protected void requestLopHoc(final String uid, final String databaseKey, final ArrayList<ItemLop> itemArr) {
        isRefreshing = true;

        RequestServer requestServer = new RequestServer(getActivity(), Request.Method.GET, AppConfig.URL_GET_LOPKHOAHOC);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    itemArr.clear();

                    JSONArray jsonLopHocArray = response.getJSONObject("data").getJSONArray("classes");

                    for (int i = 0; i < jsonLopHocArray.length(); i++) {
                        JSONObject lopHoc = jsonLopHocArray.getJSONObject(i);
                        String id = lopHoc.getString("id");
                        String idLop = lopHoc.getString("code");
                        String nameLop = lopHoc.getString("name");
//                        String baseLop = lopHoc.getString("base");
                        int soSV = lopHoc.getInt("student_count");
                        String nameGiangVien = lopHoc.getString("teacher_name");

                        Log.i(TAG, "id: " + id);
                        Log.i(TAG, "idLop: " + idLop);
                        Log.i(TAG, "name lop: " + nameLop);
                        Log.i(TAG, "so sv: " + soSV);

//                        JSONObject jsonGiangVien = jsonLopHocArray.getJSONObject(i).getJSONObject("teacher");
//
//                        String nameGiangVien = jsonGiangVien.getString("name");
//                        String idGiangVien = jsonGiangVien.getString("id");
//                        String emailGiangVien = jsonGiangVien.getString("email");
//                        String typeGiangVien = jsonGiangVien.getString("type");

                        itemArr.add(new ItemLop(nameLop, id, idLop, nameGiangVien, soSV));
                    }

//                    Toast.makeText(mContext, "Lấy dữ liệu môn học thành công!", Toast.LENGTH_LONG).show();
                    Message msg = new Message();
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                } else {
                    // Error occurred in registration. Get the error message
//                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(mContext,
                            message, Toast.LENGTH_LONG).show();

                    isRefreshing = false;
                    onFail();
                }
            }
        });

        if (!requestServer.sendRequest("get classes")){
            isRefreshing = false;
            if (swipeRefresh.isRefreshing()){
                swipeRefresh.setRefreshing(false);
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isRefreshing = false;
            onPostComplete();
        }
    };

    public boolean isRefreshing() {
        return isRefreshing;
    }

    protected abstract void onFail();

    protected abstract void onPostComplete(
    );
}
