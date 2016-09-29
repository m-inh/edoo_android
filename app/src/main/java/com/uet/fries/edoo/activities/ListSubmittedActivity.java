package com.uet.fries.edoo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.adapter.StudentSubmittedExerciseAdapter;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.utils.CommonVLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tmq on 29/09/2016.
 */

public class ListSubmittedActivity extends AppCompatActivity {
    private static final String TAG = ListSubmittedActivity.class.getSimpleName();
    public StudentSubmittedExerciseAdapter adapter;
    private String studentCount;
    private TextView tvPercent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_statistic_submit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        studentCount = getIntent().getStringExtra("student_count");
        initViews();
    }

    private void initViews() {
        adapter = new StudentSubmittedExerciseAdapter(this);

        tvPercent = (TextView) findViewById(R.id.tv_percent_submit);
        tvPercent.setVisibility(View.INVISIBLE);

        ListView lvStudentSubmitted = (ListView) findViewById(R.id.lv_list_student_submitted);
        lvStudentSubmitted.setAdapter(adapter);
        Spinner spFilter = (Spinner) findViewById(R.id.sp_filter_submitted);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_opts_statistic_submitted, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(adapter);

        requestGetCheckEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submitted_exercise_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                    Log.i(TAG, data.toString());
                    JSONArray attachFiles = data.getJSONArray("attack_files") ;
                    ArrayList<StudentSubmittedExerciseAdapter.Student> arrStudent = new ArrayList<>();

                    for (int i = 0; i < attachFiles.length(); i++) {
                        JSONObject file = attachFiles.getJSONObject(i);
                        JSONObject author = file.getJSONObject("author");
                        String name = author.getString("name");
                        String createAt = file.getString("created_at");

                        arrStudent.add(new StudentSubmittedExerciseAdapter.Student(name, CommonVLs.getDateTime2(createAt), 1));
                    }

                    adapter.setArrStudent(arrStudent);
                    adapter.notifyDataSetChanged();
//                    tvPercent.setText(arrStudent + "/" + studentCount);
                } else {
                    Log.d(TAG, "Error get list Student submitted exercise");
                }
            }
        });
        requestServer.sendRequest("get list student submitted exercise");
    }
}
