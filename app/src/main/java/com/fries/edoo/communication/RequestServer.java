package com.fries.edoo.communication;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fries.edoo.app.AppController;
import com.fries.edoo.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tmq on 12/07/2016.
 */
public class RequestServer {
    private static final String TAG = "RequestServer";
    private JsonObjectRequest request;

    private int method;
    private String url;
    private JSONObject jsonReq;

    private SessionManager session;

    // Request: Not upload data (JSONObject)
    public RequestServer(Context context, int method, String url) {
        session = new SessionManager(context);
        this.method = method;
        this.url = url;
        this.jsonReq = new JSONObject();

        initListener();
    }

    // Request: Upload data (JSONObject)
    public RequestServer(Context context, int method, String url, JSONObject jsonReq) {
        session = new SessionManager(context);
        this.method = method;
        this.url = url;
        this.jsonReq = jsonReq;

        initListener();
    }

    private void initListener() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mListener != null){
                        mListener.onReceive(false, response, response.getString("message"));
                    }
                    Log.i(TAG, "Response = " + response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    byte[] data = error.networkResponse.data;
                    JSONObject jsonError = new JSONObject(new String(data));
                    if (mListener != null){
                        mListener.onReceive(true, jsonError, jsonError.getString("message"));
                    }
                    Log.i(TAG, "Response = " + jsonError);
                } catch (JSONException e) {
                    Log.i(TAG, "Error json: " + e.toString());
                }
            }
        };

        final boolean isLoggin = session.isLoggedIn();
        final String token = session.getTokenLogin();

        request = new JsonObjectRequest(method, url, jsonReq, listener, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.i(TAG, "Set header");
//                Log.i(TAG, "" + isLoggin);
//                Log.i(TAG, "Token: " + token);
                if (isLoggin) {
                    Map<String, String> params = new HashMap<String, String>();
                    // Send token to server -> finish a session
                    params.put("Authorization", token);
//                    Log.i(TAG, "Token: " + token);
                    return params;
                }

                return super.getHeaders();
            }
        };
    }

    public void sendRequest(String tag) {
        AppController.getInstance().addToRequestQueue(request, tag);
    }


    // ---------------------------------------------------------------------------------------------
    private ServerListener mListener;

    public void setListener(ServerListener listener) {
        mListener = listener;
    }

    public interface ServerListener {
        void onReceive(boolean error, JSONObject response, String message) throws JSONException;
    }

}
