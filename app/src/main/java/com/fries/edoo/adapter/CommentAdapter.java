package com.fries.edoo.adapter;//package com.hackathon.fries.myclass.adapter;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.hackathon.fries.myclass.R;
//import com.hackathon.fries.myclass.app.AppConfig;
//import com.hackathon.fries.myclass.app.AppController;
//import com.hackathon.fries.myclass.helper.SQLiteHandler;
//import com.hackathon.fries.myclass.models.ItemComment;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Le Tuan on 21-Nov-15.
// */
//public class CommentAdapter extends BaseAdapter {
//    private static final String TAG = "CommentAdapter";
//    private ArrayList<ItemComment> listComments;
//    private Context mContext;
//    private LayoutInflater lf;
//    private ProgressDialog pDialog;
//
//    public CommentAdapter(Context context, ArrayList<ItemComment> arr) {
//        listComments = arr;
//        mContext = context;
//        lf = LayoutInflater.from(mContext);
//        pDialog = new ProgressDialog(context);
//
////        getDataComments();
//    }
//
//    public void addComment(ItemComment cmt) {
//        listComments.add(cmt);
//    }
//
//
//    @Override
//    public int getCount() {
//        return listComments.size();
//    }
//
//    @Override
//    public ItemComment getItem(int position) {
//        return listComments.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View view, ViewGroup parent) {
//        if (view == null) {
//            view = lf.inflate(R.layout.item_comment_in_popup, null);
//        }
//
//        ItemComment item = listComments.get(position);
//
//        ImageView img = (ImageView) view.findViewById(R.id.imgAvaComment);
//        TextView txtUser = (TextView) view.findViewById(R.id.txtUserName);
//        TextView txtContent = (TextView) view.findViewById(R.id.txtContentComment);
//        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_comment);
//
//        txtUser.setText(item.getName());
//        txtContent.setText(item.getContent());
//        checkBox.setChecked(item.isVote());
//
//        //get user
//        SQLiteHandler db = new SQLiteHandler(mContext);
//        HashMap<String, String> user = db.getUserDetails();
//        final String uid = user.get("uid");
//        String type = user.get("type");
//
//
//        if (type.equalsIgnoreCase("student") || checkBox.isChecked()) {
//            checkBox.setClickable(false);
//        }
//
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(mContext, "Câu trả lời của sinh viên được ủng hộ", Toast.LENGTH_LONG).show();
//
//                postVoteByTeacher(listComments.get(position).getIdComment(), uid);
//
//                mListener.onVote(checkBox.isChecked());
//
//                buttonView.setClickable(false);
//            }
//        });
//
//        Bitmap bitmap = getImageBitmapFromUrl(item.getAvaUrl());
//        if (bitmap != null) img.setImageBitmap(bitmap);
//
//        return view;
//    }
//
//    public Bitmap getImageBitmapFromUrl(String path) {
//        Bitmap bm = null;
//        try {
//            URL url = new URL(path);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            if (conn.getResponseCode() != 200) {
//                return bm;
//            }
//            conn.connect();
//            InputStream is = conn.getInputStream();
//
//            BufferedInputStream bis = new BufferedInputStream(is);
//            try {
//                bm = BitmapFactory.decodeStream(bis);
//            } catch (OutOfMemoryError ex) {
//                bm = null;
//            }
//            bis.close();
//            is.close();
//        } catch (Exception e) {
//        }
//
//        return bm;
//    }
//
//    private void postVoteByTeacher(final String idCmt, final String uid) {
//        showDialog();
//        Log.i(TAG, idCmt);
//        Log.i(TAG, uid);
//
//        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_VOTE_COMMENT,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        hideDialog();
//
//                        Log.i(TAG, response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            boolean error = jsonObject.getBoolean("error");
//                            if (!error) {
//                                // cap nhat giao dien
//                                // thong bao dang bai thanh cong
////                                JSONObject jsonComment = jsonObject.getJSONObject("comment");
////                                String idCmt = jsonComment.getString("id");
//
////                                Bundle b = new Bundle();
////                                b.putString("idCmt", idCmt);
////                                b.putString("content", content);
//
//                                Message msg = new Message();
////                                msg.setData(b);
//                                msg.setTarget(mHandler);
//                                msg.sendToTarget();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                hideDialog();
//                Log.i(TAG, "Vote error: " + error.getMessage());
//                Toast.makeText(mContext, error.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> data = new HashMap<>();
//                data.put("user", uid);
//                data.put("id", idCmt);
//
//                return data;
//            }
//        };
//
//        AppController.getInstance().addToRequestQueue(request, "post comment");
//    }
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };
//
//    private void showDialog() {
//        if (!pDialog.isShowing()) {
//            pDialog.show();
//        }
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing()) {
//            pDialog.hide();
//        }
//    }
//
//    //----------------------- Interface -------------------------------------------
//    private OnVoteListener mListener;
//
//    public void setOnVoteListener(OnVoteListener listener) {
//        mListener = listener;
//    }
//
//    public interface OnVoteListener {
//        public void onVote(boolean isVote);
//    }
//}
