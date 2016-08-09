package com.fries.edoo;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.helper.SQLiteHandler;
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
    private static final String TAG = "EditProfileActivity";

    private EditText edtName, edtEmail, edtLopKhoaHoc, edtMssv;
    private CircleImageView ivAvar;
    private Button btnDone;
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        //init data base
        db = new SQLiteHandler(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        initViews();
    }

    private void initViews() {
        edtName = (EditText) findViewById(R.id.edt_editname);
        edtMssv = (EditText) findViewById(R.id.edt_editmssv);
        edtEmail = (EditText) findViewById(R.id.edt_editemail);
        edtLopKhoaHoc = (EditText) findViewById(R.id.edt_editlop);
        ivAvar = (CircleImageView) findViewById(R.id.iv_editava);

        //Get intent from MainActivity
        Intent mIntent = getIntent();
        edtName.setText(mIntent.getStringExtra("name"));
        edtEmail.setText(mIntent.getStringExtra("email"));
        edtLopKhoaHoc.setText(mIntent.getStringExtra("lop"));
        edtMssv.setText(mIntent.getStringExtra("mssv"));

        ivAvar.setFillColor(Color.WHITE);

        SQLiteHandler sqLite = new SQLiteHandler(this);
        HashMap<String, String> user = sqLite.getUserDetails();
        String urlAvatar = user.get("avatar");
//        String pathSaveImage = MainActivity.PATH_TO_DIR_SAVING_IMAGE + getIntent().getStringExtra("mssv") + ".jpg";
//        UserPicture.setCurrentAvatar(ivAvar, pathSaveImage);
        Picasso.with(this).load(urlAvatar).placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user).into(ivAvar);
//        UserPicture.setCurrentAvatar(MainActivity.ivAva, pathSaveImage);

        ivAvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // in onCreate or any event where your want the user to
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,
//                        getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
                // The library provides a utility method to start an image picker:
                Crop.pickImage(EditProfileActivity.this);
            }
        });

        edtName.setInputType(InputType.TYPE_NULL);
        edtMssv.setInputType(InputType.TYPE_NULL);
        edtEmail.setInputType(InputType.TYPE_NULL);
        edtLopKhoaHoc.setInputType(InputType.TYPE_NULL);

        btnDone = (Button) findViewById(R.id.btn_editdone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String name = edtName.getText().toString();
//                String lop = edtLopKhoaHoc.getText().toString();
//                String mssv = edtMssv.getText().toString();

//                if (!name.equalsIgnoreCase("") && !lop.equalsIgnoreCase("")) {
//
//                    updateUser(name, lop, mssv, edtEmail.getText().toString());
//                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }

//    private void updateUser(final String name, final String lop, final String mssv, final String email) {
//        // Tag used to cancel the request
//        String tag_string_req = "req_updateuser";
//
//        pDialog.setMessage("Cập nhật ...");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_UPDATE, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Update Response: " + response.toString());
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//                    if (!error) {
//                        // User successfully stored in MySQL
//                        // Now store the user in sqlite
//                        String uid = jObj.getString("uid");
//
//                        JSONObject user = jObj.getJSONObject("user");
//                        String name = user.getString("name");
//                        String email = user.getString("email");
//                        String created_at = user
//                                .getString("created_at");
//                        String lop = user.getString("lop");
//                        String mssv = user.getString("mssv");
//                        String type = user.getString("type");
//                        String ava = user.getString("ava");
//
//
////                        String lop = "K58CLC";
////                        String mssv = "13020285";
////                        String type = "Sinh viên";
//
//                        Log.i(TAG, "update name: " + name);
//                        Log.i(TAG, "update email: " + email);
//                        Log.i(TAG, "update createat: " + created_at);
//                        Log.i(TAG, "update lop: " + lop);
//                        Log.i(TAG, "update mssv: " + mssv);
//                        Log.i(TAG, "update type: " + type);
//
//                        if (type.equalsIgnoreCase("student")) {
//                            type = "Sinh viên";
//                        } else if (type.equalsIgnoreCase("teacher")) {
//                            type = "Giảng viên";
//                        }
//
//                        // Inserting row in users table
//                        db.deleteUsers();
//                        db.addUser(name, email, uid, created_at, lop, mssv, type, ava);
//
//                        // Return mainActivity
//                        //Tra intent ve MainActivity
//                        Intent mIntent = new Intent();
//                        mIntent.putExtra("name", name);
//                        mIntent.putExtra("lop", lop);
//                        mIntent.putExtra("mssv", mssv);
//                        mIntent.putExtra("type", type);
//                        setResult(RESULT_OK, mIntent);
//                        Toast.makeText(getApplicationContext(), "Cập nhật thông tin thành công :)", Toast.LENGTH_LONG).show();
//                        finish();
//                    } else {
//
//                        // Error occurred in registration. Get the error
//                        // message
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
//                        setResult(RESULT_CANCELED);
////                      finish();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Registration Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//                finish();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting params to register url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("name", name);
//                params.put("email", email);
////                params.put("password", password);
//                params.put("lop", lop);
//                params.put("mssv", mssv);
//
//
//                return params;
//            }
//        };
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
                ivAvar.setImageBitmap(mBitmap);
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

    private void uploadImage(final Bitmap bmp) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_POST_IMG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "response: " + response);
                        //Disimissing the progress dialog
                        loading.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String urlAva = jsonObject.getString("url");

                            //Send url avatar to handler
                            Message msg = new Message();
                            Bundle b = new Bundle();
                            b.putString("avatar", urlAva);
                            msg.setData(b);
                            msg.setTarget(mHandler);
                            msg.sendToTarget();

                            //Showing toast message of the response
//                        Toast.makeText(EditProfileActivity.this, response, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
//                        Toast.makeText(EditProfileActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bmp);

                //Getting Image Name
//                String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();


                SQLiteHandler sqLite = new SQLiteHandler(EditProfileActivity.this);
                Map<String, String> user = sqLite.getUserDetails();
                //Adding parameters
                params.put("avatar", image);
                params.put("uid", user.get(SQLiteHandler.KEY_UID));

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
//        requestQueue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

//        Log.i(TAG, encodedImage);
        return encodedImage;
    }

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










