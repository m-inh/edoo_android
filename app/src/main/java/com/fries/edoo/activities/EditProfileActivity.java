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
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private TextView txtName, txtEmail, txtRegularClass, txtCode;
    private CircleImageView ivAvatar;
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

        SQLiteHandler sqLite = new SQLiteHandler(this);
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

        txtName.setInputType(InputType.TYPE_NULL);
        txtCode.setInputType(InputType.TYPE_NULL);
        txtEmail.setInputType(InputType.TYPE_NULL);
        txtRegularClass.setInputType(InputType.TYPE_NULL);

        btnDone = (Button) findViewById(R.id.btn_edit_done);

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










