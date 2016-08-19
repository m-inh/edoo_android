package com.fries.edoo.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fries.edoo.R;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.richeditor.RichEditor;

import java.util.HashMap;

public class PostWriterActivity extends AppCompatActivity {

    private static final String TAG = PostWriterActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    private EditText edtContentPost;
    private EditText edtTitlePost;

    private String idLop;

    private SQLiteHandler sqlite;

    private boolean isAllowedClick;

    //---------------------
    private RichEditor mEditor;
    private int textSizeEditor;
    //---------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_writer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get data from Mainactivity
        Intent mIntent = getIntent();
        this.idLop = mIntent.getStringExtra("class_id");

        sqlite = new SQLiteHandler(this);

        isAllowedClick = true;

        textSizeEditor = 0;

        initViews();
    }

    private void initViews() {
        edtTitlePost = (EditText) findViewById(R.id.edt_titlePost);
        edtContentPost = (EditText) findViewById(R.id.edt_contentPost);

        initViewsRichEditor();
    }

    private void initViewsRichEditor() {
        mEditor = (RichEditor) findViewById(R.id.editor_rich_editor);
//        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(8, 8, 8, 8);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Write post here ...");

//        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
//            @Override
//            public void onTextChange(String text) {
//                mPreview.setText(text);
//                mWebView.getSettings().setJavaScriptEnabled(true);
//                mWebView.loadData(text, "text/html", "UTF-8");
//            }
//        });

        findViewById(R.id.editor_action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.editor_action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.editor_action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.editor_action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });


        findViewById(R.id.editor_action_text_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSizeEditor = (textSizeEditor + 1) % 3;
                mEditor.setHeading((textSizeEditor + 1) * 2);
            }
        });

        findViewById(R.id.editor_action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                        "dachshund");
            }
        });

        findViewById(R.id.editor_action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInsertLink();
            }
        });

        mEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                HorizontalScrollView editorToolBar = (HorizontalScrollView) findViewById(R.id.editor_tool_bar);
                if (b) {
                    editorToolBar.setVisibility(View.VISIBLE);
                } else {
                    editorToolBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.writepost_menu, menu);
        menu.findItem(R.id.action_post).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isAllowedClick) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_post:
                String title = edtTitlePost.getText().toString();
                String content = edtContentPost.getText().toString();
                if (content.isEmpty()) {
                    Toast.makeText(PostWriterActivity.this, "Bài viết không có nội dung!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (title.isEmpty()) {
                    Toast.makeText(PostWriterActivity.this, "Bài viết không có tiêu đề!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //block button post when clicked
                isAllowedClick = false;

                //get intent tu MainActivity
//                Intent intent = getIntent();
                boolean isTeacher = (sqlite.getUserDetails().get("type").equalsIgnoreCase("teacher"));
                postPost(idLop, title, content, "note", false, isTeacher);
                break;
        }
        return true;
    }

    private void showDialogInsertLink() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogAnimation);
        dialog.setTitle(R.string.dialog_title_insert_link);
        dialog.setIcon(R.drawable.ic_editor_insert_link_color);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_insert_link_editor, null);
        dialog.setView(view);

        dialog.setPositiveButton(R.string.insert, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText link = (EditText) view.findViewById(R.id.edt_insert_url);
                EditText linkTitle = (EditText) view.findViewById(R.id.edt_insert_url_title);

                String txtLink = link.getText().toString();
                String txtLinkTitle = linkTitle.getText().toString();

                if (txtLink.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Link rỗng", Toast.LENGTH_SHORT).show();
                } else {
                    if (txtLinkTitle.isEmpty()) {
                        mEditor.insertLink(txtLink, txtLink);
                    }else mEditor.insertLink(txtLink, txtLinkTitle);
                }
            }
        });

        dialog.show();
    }

    private void postPost(String classId, String title, String content, String type, boolean isIncognito, boolean isPostTeacher) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.show();

        String url = AppConfig.URL_POST_POST;

        JSONObject params = new JSONObject();
        try {
            params.put("class_id", classId);
            params.put("title", title);
            params.put("content", content);
            params.put("type", type);
            params.put("is_incognito", isIncognito);
            params.put("is_post_teacher", isPostTeacher);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(this, Request.Method.POST, url, params);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                pDialog.dismiss();
                if (!error) {
                    Log.i(TAG, response.toString());

                    Message msg = new Message();
                    msg.setTarget(mHandler);
                    msg.sendToTarget();
                } else {
//                    ivPost.setClickable(true);
                    isAllowedClick = true;
                    Log.i(TAG, "Post error: " + message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });

        requestServer.sendRequest("post new post");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setResult(RESULT_OK);
            finish();
        }
    };

}
