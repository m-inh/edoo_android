package com.uet.fries.edoo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.adapter.StudentSubmittedExerciseAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tmq on 29/09/2016.
 */

public class ListSubmittedExercise extends AppCompatActivity {
    private static final String TAG = ListSubmittedExercise.class.getSimpleName();
    public StudentSubmittedExerciseAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_statistic_submit);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(R.string.txt_list_submitted);

        initViews();
    }

    private void initViews() {
        adapter = new StudentSubmittedExerciseAdapter(this);

        ListView lvStudentSubmitted = (ListView) findViewById(R.id.lv_list_student_submitted);
        lvStudentSubmitted.setAdapter(adapter);

        requestGetCheckEvent();
    }

    private void requestGetCheckEvent() {
        String postId = getIntent().getStringExtra("post_id");
        if (postId == null) return;

        RequestServer requestServer = new RequestServer(this, Request.Method.GET, AppConfig.URL_CHECK_EVENT + postId);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                if (!error) {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray attachFiles = data.getJSONArray("attack_files") ;
                    ArrayList<StudentSubmittedExerciseAdapter.Student> arrStudent = new ArrayList<>();

                    for (int i = 0; i < attachFiles.length(); i++) {
                        JSONObject file = attachFiles.getJSONObject(i);
                        JSONObject author = file.getJSONObject("author");
                        String name = author.getString("name");

                        arrStudent.add(new StudentSubmittedExerciseAdapter.Student(name, "", 1));
                    }

                    adapter.setArrStudent(arrStudent);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error get list Student submitted exercise");
                }
            }
        });
        requestServer.sendRequest("get list student submitted exercise");
    }
}
