package com.uet.fries.edoo.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.helper.PrefManager;
import com.uet.fries.edoo.utils.CommonVLs;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_REGISTER = 1234;
    private Button btnLogin, btnLinkToRegister, btnForgotPass;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private PrefManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnForgotPass = (Button) findViewById(R.id.btn_forgot_password);

        //lay du lieu tu intent do vao edittext
//        Intent mIntent = getIntent();
//        inputEmail.setText(mIntent.getStringExtra(SQLiteHandler.KEY_EMAIL));
//        inputPassword.setText(mIntent.getStringExtra("password"));

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new PrefManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialogForgotPass();
                showDialogForgotPass_temp();
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

        if (!CommonVLs.isHasNetworkPermissions(this)) {
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

//                    Log.i(TAG, "login: " + name);
//                    Log.i(TAG, "ava: " + ava);
//                    Log.i(TAG, "login: " + email);
//                    Log.i(TAG, "login: " + lop);
//                    Log.i(TAG, "login: " + mssv);
//                    Log.i(TAG, "login: " + type);

                    // Inserting row in users table
//                  db.addUser(name, email, uid, created_at, lop, mssv, type, ava);

                    // user successfully logged in
                    // Create login session
                    session.setLogin(true);
                    String token = response.getJSONObject("data").getString("token");
                    session.setTokenLogin(token);

                    // Temporary data
                    db.addUser(name, email, uid, "", lop, mssv, type, ava);

                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

                pDialog.dismiss();
            }
        });
        if (!requestServer.sendRequest("req_log_in")) {
            pDialog.dismiss();
//            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }

    }

    private void showDialogForgotPass_temp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Chức năng đang được phát triển. \nĐể khôi phục mật khẩu, bạn vui lòng liên hệ với đội phát triển thông qua mail:\nFries.uet@gmail.com");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    private void showDialogForgotPass() {
        final Dialog dialog = new Dialog(this, R.style.DialogInputActionBar);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null);
        dialog.setContentView(layout);
        dialog.setTitle("Reset password");
        dialog.setCancelable(false);

        final EditText edtCode = (EditText) layout.findViewById(R.id.edt_code_forget_pass);
        final EditText edtEmail = (EditText) layout.findViewById(R.id.edt_email_forget_pass);
        Button btnCancel = (Button) layout.findViewById(R.id.btn_cancel_forgot_pass);
        Button btnSend = (Button) layout.findViewById(R.id.btn_send_forgot_pass);

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel_forgot_pass:
                        dialog.dismiss();
                        break;
                    case R.id.btn_send_forgot_pass:
                        String code = edtCode.getText().toString();
                        String email = edtEmail.getText().toString();
                        if (code.isEmpty() || email.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        } else {
                            requestResetPassword(email, code);
                            dialog.dismiss();
                        }
                        break;
                }
            }
        };
        btnCancel.setOnClickListener(onClick);
        btnSend.setOnClickListener(onClick);

        dialog.show();
    }

    private void requestResetPassword(String email, String code){
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestServer requestServer = new RequestServer(this, Method.POST, AppConfig.URL_RESET_PASSWORD, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                Log.i(TAG, "res = " + response.toString());
                if (!error){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Kiểm tra email để lấy mật khẩu mới.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } else {
                    Log.d(TAG, "Request Error");
                }
            }
        });
        requestServer.sendRequest("reset_password");
    }
}
