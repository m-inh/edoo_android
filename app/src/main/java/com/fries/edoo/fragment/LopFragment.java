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

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_LOPKHOAHOC, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                itemArr.clear();
                Log.i(TAG, "Get lop hoc Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONArray jsonLopHocArray = jObj.getJSONArray("group");

                        for (int i = 0; i < jsonLopHocArray.length(); i++) {
                            JSONObject lopHoc = jsonLopHocArray.getJSONObject(i);
                            String id = lopHoc.getString("id");
                            String idLop = lopHoc.getString("name");
                            String nameLop = lopHoc.getString("name");
                            String baseLop = lopHoc.getString("base");
                            int soSV = lopHoc.getInt("soSV");

                            Log.i(TAG, "id: " + id);
                            Log.i(TAG, "idLop: " + idLop);
                            Log.i(TAG, "name lop: " + nameLop);
                            Log.i(TAG, "so sv: " + soSV);

                            JSONObject jsonGiangVien = jsonLopHocArray.getJSONObject(i).getJSONObject("teacher");

                            String nameGiangVien = jsonGiangVien.getString("name");
                            String idGiangVien = jsonGiangVien.getString("id");
                            String emailGiangVien = jsonGiangVien.getString("email");
                            String typeGiangVien = jsonGiangVien.getString("type");

                            itemArr.add(new ItemLop(nameLop, id, idLop, nameGiangVien, soSV));
                        }
                        Toast.makeText(mContext, "Lấy dữ liệu môn học thành công!", Toast.LENGTH_LONG).show();
                        Message msg = new Message();
                        msg.setTarget(mHandler);
                        msg.sendToTarget();
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(mContext,
                                errorMsg, Toast.LENGTH_LONG).show();

                        isRefreshing = false;
                        onFail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isRefreshing = false;
                    onFail();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get class Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                isRefreshing = false;
                onFail();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", uid);
                params.put("base", databaseKey);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "Get lop hoc");

    }

    private Handler mHandler = new Handler(){
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
