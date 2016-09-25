package com.uet.fries.edoo.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.adapter.ProfileAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.MultipartRequest;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.models.ItemUser;
import com.uet.fries.edoo.utils.CommonVLs;
import com.uet.fries.edoo.utils.Reporter;
import com.uet.fries.edoo.utils.UserPicture;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 23423;

    private ProfileAdapter adapter;
    private RecyclerView rvListInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Reporter.register(this);// Crash Reporter

        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        getProfileServer();
    }

    private void initViews() {
        rvListInfo = (RecyclerView) findViewById(R.id.rv_list_info);
        rvListInfo.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
//            case R.id.action_edit_profile:
//                Toast.makeText(this, "Edit profile", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.action_change_avatar:
                pickImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // ----------------------

    private void pickImage() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        CharSequence[] selection = {getString(R.string.txt_gallery), getString(R.string.txt_camera)};
        dialog.setItems(selection, this);
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i==0) Crop.pickImage(this);
        else pickImageFromCamera();
    }

    private Uri photoUri;

    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        photoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        if (CommonVLs.isHasCameraPermissions(this)) {
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            CommonVLs.verifyCameraPermissions(this);
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Listen for the result of the crop:
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            beginCrop(photoUri);
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
//                ivAvatar.setImageBitmap(mBitmap);

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
        File f = null;
        FileOutputStream fo = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
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


    // ---------------------------------------------------------------------------------------------
    public void getProfileServer() {
        RequestServer requestServer = new RequestServer(this, Request.Method.GET, AppConfig.URL_GET_PROFILE);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    JSONObject data = response.getJSONObject("data");
//                    Log.i(TAG, "Profile = " + data.toString());
                    adapter = new ProfileAdapter(ProfileActivity.this, new ItemUser(data));
                    rvListInfo.setAdapter(adapter);
                }
            }
        });

        requestServer.sendRequest("get_profile");
    }

    public void updateProfile(JSONObject params) {
        RequestServer requestServer = new RequestServer(this, Request.Method.POST, AppConfig.URL_GET_PROFILE, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
//                    Log.i(TAG, "update profile, res = " + response.toString());
                    JSONObject data = response.getJSONObject("data");
                    String description = data.getString("description");
                    String favorite = data.getString("favorite");
                    adapter.updateDataInfo(description, favorite);
                }
            }
        });
        requestServer.sendRequest("update_profile");
    }

    private void uploadImage(final Bitmap bmp) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        byte[] fileData = CommonVLs.getFileDataFromBitmap(bmp);
        String filename = "avatar.jpg";
        String fileType = "image/jpg";
        MultipartRequest request =
                new MultipartRequest(this, Request.Method.POST,
                        AppConfig.URL_POST_IMG_AVATAR, fileData, filename, fileType);

        request.setListener(new MultipartRequest.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
//                Log.d(TAG, "response: " + response);
//                Log.d(TAG, "msg: " + message);
                //Disimissing the progress dialog
                loading.dismiss();

                if (!error) {
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

        if (!request.sendRequest("update avatar")) {
            loading.dismiss();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Save new user with link to avatar to sqlite
            SQLiteHandler sqLite = new SQLiteHandler(ProfileActivity.this);

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

            adapter.updateAvatar(newAva);
            //exit activity with result ok, reload avatar
            setResult(RESULT_OK);
        }
    };

}
