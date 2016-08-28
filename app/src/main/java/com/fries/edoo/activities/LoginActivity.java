package com.fries.edoo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.fries.edoo.R;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.helper.SessionManager;
import com.fries.edoo.utils.CommonVLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_REGISTER = 1234;
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        //lay du lieu tu intent do vao edittext
//        Intent mIntent = getIntent();
//        inputEmail.setText(mIntent.getStringExtra(SQLiteHandler.KEY_EMAIL));
//        inputPassword.setText(mIntent.getStringExtra("password"));

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        FirebaseInstanceId.getInstance().deleteInstanceId();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
//        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        RegisterActivity.class);
////                startActivity(i);
//                startActivityForResult(i, REQUEST_CODE_REGISTER);
//            }
//        });

        btnLinkToRegister.setVisibility(View.INVISIBLE);

        if (!CommonVLs.isHasNetworkPermissions(this)){
            CommonVLs.verifyInternetStatePermissions(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_CANCELED) {
                return;
            } else if (resultCode == RESULT_OK) {
                inputEmail.setText(data.getStringExtra(SQLiteHandler.KEY_EMAIL));
                inputPassword.setText(data.getStringExtra("password"));
            }
        }
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Đăng nhập ...");
        pDialog.show();
//        showDialog();

        JSONObject objRequest = new JSONObject();
        try {
            objRequest.put("email", email);
            objRequest.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(getApplicationContext(), Method.POST, AppConfig.URL_LOGIN, objRequest);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    // Now store the user in SQLite
                    JSONObject user = response.getJSONObject("data").getJSONObject("user");
                    String ava = user.getString("avatar");
                    String email = user.getString("email");
                    String uid = user.getString("id");
                    String lop = user.getString("regular_class");
                    String mssv = user.getString("code");
                    String type = user.getString("capability");
                    String name = user.getString("name");

//                    String created_at = "";

                    Log.i(TAG, "login: " + name);
                    Log.i(TAG, "ava: " + ava);
                    Log.i(TAG, "login: " + email);
                    Log.i(TAG, "login: " + lop);
                    Log.i(TAG, "login: " + mssv);
                    Log.i(TAG, "login: " + type);

                    // Inserting row in users table
//                  db.addUser(name, email, uid, created_at, lop, mssv, type, ava);

                    // user successfully logged in
                    // Create login session
                    session.setLogin(true);
                    String token = response.getJSONObject("data").getString("token");
                    session.setTokenLogin(token);

                    // Temporary data
                    db.addUser(name, email, uid, "", lop, mssv, type, ava);

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, message);
                }

                pDialog.dismiss();
            }
        });
        if (!requestServer.sendRequest("req_log_in")){
            pDialog.dismiss();
//            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }

    }
}
