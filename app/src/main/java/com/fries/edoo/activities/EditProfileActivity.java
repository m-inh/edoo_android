package com.fries.edoo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.R;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.MultipartRequest;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.utils.CommonVLs;
import com.fries.edoo.utils.UserPicture;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends Activity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private TextView txtName, txtEmail, txtRegularClass, txtCode;
    private CircleImageView ivAvatar;
    private Button btnDone;
    private ProgressDialog pDialog;
    private SQLiteHandler sqLite;
    private boolean isTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sqLite = new SQLiteHandler(this);

        initViews();
        initViewsForTeacher();
        setUserVoteSolve();
    }

    private void initViews() {
        txtName = (TextView) findViewById(R.id.txt_name_profile);
        txtCode = (TextView) findViewById(R.id.txt_code_profile);
        txtEmail = (TextView) findViewById(R.id.txt_email_profile);
        txtRegularClass = (TextView) findViewById(R.id.txt_regular_class_profile);
        ivAvatar = (CircleImageView) findViewById(R.id.iv_edit_avatar);

        //Get intent from MainActivity
        Intent mIntent = getIntent();
        txtName.setText(mIntent.getStringExtra("name"));
        txtEmail.setText(mIntent.getStringExtra("email"));
        txtRegularClass.setText(mIntent.getStringExtra("lop"));
        txtCode.setText(mIntent.getStringExtra("mssv"));

        ivAvatar.setFillColor(Color.WHITE);

        HashMap<String, String> user = sqLite.getUserDetails();
        String urlAvatar = user.get("avatar");
//        String pathSaveImage = MainActivity.PATH_TO_DIR_SAVING_IMAGE + getIntent().getStringExtra("mssv") + ".jpg";
//        UserPicture.setCurrentAvatar(ivAvar, pathSaveImage);
        Picasso.with(this)
                .load(urlAvatar).fit()
                .placeholder(R.mipmap.ic_user)
                .error(R.mipmap.ic_user).into(ivAvatar);
//        UserPicture.setCurrentAvatar(MainActivity.ivAva, pathSaveImage);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(EditProfileActivity.this);
            }
        });

        btnDone = (Button) findViewById(R.id.btn_edit_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            setResult(RESULT_OK);
            finish();
            }
        });
    }

    private void initViewsForTeacher() {
        isTeacher = (sqLite.getUserDetails().get("type").equalsIgnoreCase("teacher"));
        if (!isTeacher) return;

        TextView hintCode = (TextView) findViewById(R.id.txt_hint_code_profile);
        TextView hintRegularClass = (TextView) findViewById(R.id.txt_hint_regular_class_profile);

        hintCode.setText(R.string.hint_msgv);
        hintRegularClass.setText(R.string.covanlop);

        // ---------
        hintRegularClass.setVisibility(View.GONE);
        txtRegularClass.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    // Listen for the result of the crop:
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = Crop.getOutput(result);
            Bitmap mBitmap = null;
            try {
                mBitmap = new UserPicture(selectedImageUri, getContentResolver()).getBitmap();
                /*Where to save image local*/
                saveBitmapToDir(mBitmap, getIntent().getStringExtra("mssv"));
//                ivAvar.setImageURI();
                ivAvatar.setImageBitmap(mBitmap);
//                MainActivity.ivAva.setImageBitmap(mBitmap);

                // send bitmap to server
                uploadImage(mBitmap);
            } catch (IOException e) {
                Log.e(MainActivity.class.getSimpleName(), "Failed to load image", e);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            // report failure
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(MainActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }

    public void saveBitmapToDir(Bitmap mBitmap, String nameNewFile) {
//        FileInputStream in = null;
//        BufferedInputStream buf = null;
        File f = null;
        FileOutputStream fo = null;
        try {
//            in = new FileInputStream(pathOnExStorage);
//            buf = new BufferedInputStream(in);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            Bitmap mBitmap = BitmapFactory.decodeStream(buf);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            // Create a new file in external storage:
            f = new File(MainActivity.PATH_TO_DIR_SAVING_IMAGE + nameNewFile + ".jpg");
            f.createNewFile();
            // write the bytes in file:
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            // remember close de FileOuput
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUserVoteSolve() {
        RequestServer requestServer = new RequestServer(getApplicationContext(), Request.Method.GET, AppConfig.URL_GET_USER_SOLVE_VOTE);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) {
                Log.i(TAG, response.toString());
                if (error) return;

                try {
                    JSONObject data = response.getJSONObject("data");

                    int voteCount = data.getInt("vote_count");

                    TextView vote = (TextView) findViewById(R.id.txt_vote_count_profile);
                    TextView solve = (TextView) findViewById(R.id.txt_solve_count_profile);
                    ImageView ivVote = (ImageView) findViewById(R.id.iv_vote_profile);

                    vote.setText("" + voteCount);
                    solve.setText("" + data.getInt("solve_count"));

                    if (voteCount >= 0) ivVote.setImageResource(R.mipmap.ic_up_24);
                    else ivVote.setImageResource(R.mipmap.ic_down_24);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        requestServer.sendRequest("req_log_out");
    }

    private void uploadImage(final Bitmap bmp) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        byte[] fileData = CommonVLs.getFileDataFromBitmap(bmp);
        String filename = "avatar.jpg";
        String fileType = "image/jpg";
        MultipartRequest request =
                new MultipartRequest(this, Request.Method.POST,
                        AppConfig.URL_POST_IMG, fileData, filename, fileType);

        request.setListener(new MultipartRequest.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                Log.d(TAG, "response: " + response);
                Log.d(TAG, "msg: " + message);
                //Disimissing the progress dialog
                loading.dismiss();

                if (!error){
                    String urlAva = response.getJSONObject("data").getString("url");

                    //Send url avatar to handler
                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putString("avatar", urlAva);
                    msg.setData(b);
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                }
            }
        });


//        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_POST_IMG,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i(TAG, "response: " + response);
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String urlAva = jsonObject.getString("url");
//
//                            //Send url avatar to handler
//                            Message msg = new Message();
//                            Bundle b = new Bundle();
//                            b.putString("avatar", urlAva);
//                            msg.setData(b);
//                            msg.setTarget(mHandler);
//                            msg.sendToTarget();
//
//                            //Showing toast message of the response
////                        Toast.makeText(EditProfileActivity.this, response, Toast.LENGTH_LONG).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
////                        Toast.makeText(EditProfileActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //Converting Bitmap to String
//                String image = getStringImage(bmp);
//
//                //Getting Image Name
////                String name = editTextName.getText().toString().trim();
//
//                //Creating parameters
//                Map<String, String> params = new Hashtable<String, String>();
//
//
//                SQLiteHandler sqLite = new SQLiteHandler(EditProfileActivity.this);
//                Map<String, String> user = sqLite.getUserDetails();
//                //Adding parameters
//                params.put("avatar", image);
//                params.put("uid", user.get(SQLiteHandler.KEY_UID));
//
//                //returning parameters
//                return params;
//            }
//        };

        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
//        requestQueue.add(stringRequest);
//        AppController.getInstance().addToRequestQueue(stringRequest);
        request.sendRequest("update avatar");
    }

//    public String getStringImage(Bitmap bmp) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
////        Log.i(TAG, encodedImage);
//        return encodedImage;
//    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Save new user with link to avatar to sqlite
            SQLiteHandler sqLite = new SQLiteHandler(EditProfileActivity.this);

            HashMap<String, String> user = sqLite.getUserDetails();
            String name = user.get("name");
            String email = user.get("email");
            String uid = user.get("uid");
            String createAt = user.get("created_at");
            String mssv = user.get("mssv");
            String lop = user.get("lop");
            String type = user.get("type");

            String newAva = msg.getData().getString("avatar");

            sqLite.deleteUsers();
            sqLite.addUser(name, email, uid, createAt, lop, mssv, type, newAva);

            //exit activity with result ok, reload avatar
            setResult(RESULT_OK);
//            finish();
        }
    };
}