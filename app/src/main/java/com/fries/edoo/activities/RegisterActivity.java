//package com.fries.edoo.activities;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.android.volley.Request.Method;
//import com.fries.edoo.R;
//import com.fries.edoo.app.AppConfig;
//import com.fries.edoo.communication.RequestServer;
//import com.fries.edoo.helper.PrefManager;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class RegisterActivity extends Activity {
//    private static final String TAG = RegisterActivity.class.getSimpleName();
//    private Button btnRegister;
//    private Button btnLinkToLogin;
//    private EditText inputEmail;
//    private EditText inputMssv;
//    private EditText inputPassword;
////    private EditText inputRePassword;
//    private ProgressDialog pDialog;
//    private PrefManager session;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        inputMssv = (EditText) findViewById(R.id.edt_mssv);
//        inputEmail = (EditText) findViewById(R.id.email);
//        inputPassword = (EditText) findViewById(R.id.password);
////        inputRePassword = (EditText) findViewById(R.id.edt_repassword);
//        btnRegister = (Button) findViewById(R.id.btnRegister);
//        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
//
//        // Progress dialog
//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
//
//        // Session manager
//        session = new PrefManager(getApplicationContext());
//
//        // Check if user is already logged in or not
//        if (session.isLoggedIn()) {
//            // User is already logged in. Take him to main activity
//            Intent intent = new Intent(RegisterActivity.this,
//                    MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        // Register Button Click event
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                String mssv = inputMssv.getText().toString().trim() + " "; // -------- Temp: order to mssv is not empty --------------------------
//                String email = inputEmail.getText().toString().trim();
//                String password = inputPassword.getText().toString().trim();
////                String rePass = inputRePassword.getText().toString().trim();
//
////                String name = "";
////                if (!checkRePassword(password, rePass)){
////                    Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
////                } else
//                if (!mssv.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
//                    registerUser(email, password, mssv);
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        });
//
//        // Link to Login Screen
//        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
//
//    }
//
//    private boolean checkRePassword(String pass, String rePass){
//        return pass.equals(rePass);
//    }
//
//    /**
//     * Function to store user in MySQL database will post params(tag, name,
//     * email, password) to register url
//     */
//    private void registerUser(final String email, final String password, final String mssv) {
//        pDialog.setMessage("Đăng kí ...");
//        showDialog();
//
//        JSONObject objRequest = new JSONObject();
//        try {
//            objRequest.put("email", email);
//            objRequest.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestServer requestServer = new RequestServer(getApplicationContext(), Method.POST, AppConfig.URL_REGISTER, objRequest);
//        requestServer.setListener(new RequestServer.ServerListener() {
//            @Override
//            public void onReceive(boolean error, JSONObject response, String message) {
//                hideDialog();
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                if (!error) {
//                    // Return login activity
//                    Intent intent = new Intent();
//                    intent.putExtra("email", email);
//                    intent.putExtra("password", password);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            }
//        });
//        requestServer.sendRequest("req_register");
//    }
//
//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }
//}
