package com.uet.fries.edoo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uet.fries.edoo.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tmq on 29/09/2016.
 */

public class SubmittedExerciseAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater lf;
    private ArrayList<Student> arrStudent;

    public SubmittedExerciseAdapter(Context context){
        mContext = context;
        lf = LayoutInflater.from(mContext);
        arrStudent = new ArrayList<>();
    }

    public void setArrStudent(ArrayList<Student> arrStudent){
        this.arrStudent = arrStudent;
    }


    @Override
    public int getCount() {
        return arrStudent.size();
    }

    @Override
    public Student getItem(int position) {
        return arrStudent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = lf.inflate(R.layout.item_student_submitted, null);
        }

        Student student = arrStudent.get(position);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_student_name);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_submitted_time);
        CircleImageView ivStatus = (CircleImageView) convertView.findViewById(R.id.iv_submitted_status);

        ivStatus.setVisibility(View.INVISIBLE);
        tvName.setText(student.getName());
        tvTime.setText(student.getTimeSubmit());

        return convertView;
    }

    // -------------------------------- Item -------------------------------------------------------
    public static class Student {
        public static final int STATUS_SUBMITTED_ON_TIME = 1;
        public static final int STATUS_SUBMITTED_LATE = 2;
        public static final int STATUS_SUBMITTED_NONE = 3;

        private String name;
        private String timeSubmit;
        private int statusSubmit;

        public Student(String name, String timeSubmit, int statusSubmit){
            this.name = name;
            this.timeSubmit = timeSubmit;
            this.statusSubmit = statusSubmit;
        }

        public String getName() {
            return name;
        }

        public String getTimeSubmit() {
            return timeSubmit;
        }

        public int getStatusSubmit() {
            return statusSubmit;
        }
    }
}
