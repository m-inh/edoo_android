package com.fries.edoo.communication;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fries.edoo.app.AppController;
import com.fries.edoo.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TooNies1810 on 8/20/16.
 */
public class MultipartRequest {
    private VolleyMultipartRequest request;
    private int method;
    private String url;
    private byte[] file;
    private String filename;
    private String mimeType;

    private SessionManager session;

    // Request: Upload data (JSONObject)
    public MultipartRequest(Context context, int method, String url, byte[] file, String filename, String mimeType) {
        session = new SessionManager(context);
        this.method = method;
        this.url = url;
        this.file = file;
        this.filename = filename;
        this.mimeType = mimeType;

        initListener();
    }

    private void initListener() {
        Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
//                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (mListener != null){
                        mListener.onReceive(false, result, message);
                    }
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
                } catch (Exception e) {
                    if (mListener != null){
                        try {
                            mListener.onReceive(true, null, "");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };

        final boolean isLoggin = session.isLoggedIn();
        final String token = session.getTokenLogin();

        request = new VolleyMultipartRequest(url, method, listener, error){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (isLoggin) {
                    Map<String, String> params = new HashMap<String, String>();
                    // Send token to server -> finish a session
                    params.put("Authorization", token);
                    return params;
                }

                return super.getHeaders();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("file", new DataPart(filename, file, mimeType));

                return params;
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
