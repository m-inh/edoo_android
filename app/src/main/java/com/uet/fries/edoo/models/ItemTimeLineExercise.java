package com.uet.fries.edoo.models;

import com.uet.fries.edoo.utils.CommonVLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public class ItemTimeLineExercise extends ITimelineBase {

    private String remainingTime = "";
    private String percentSubmitted = "0";
    private boolean isSendFile = false;

    public ItemTimeLineExercise(){
        super();
    }

    public ItemTimeLineExercise(JSONObject jsonTimeline) throws JSONException {
        super(jsonTimeline);

        this.remainingTime = CommonVLs.getDateTime(jsonTimeline.getString("time_end"));

        try {   // Khi vao PostDetails moi co thuoc tinh is_send_file
            this.isSendFile = jsonTimeline.getBoolean("is_send_file");
        }catch (JSONException e){
            e.printStackTrace();
        }

        try {   // Khi vao PostDetails moi co thuoc tinh attach_file_count
            String countFile = jsonTimeline.getString("attach_file_count");
            int studentCount = jsonTimeline.getJSONObject("class").getInt("student_count");
            this.percentSubmitted = countFile + "/" + studentCount;
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    public String getPercentSubmitted() {
        return percentSubmitted;
    }

    public boolean getIsSendFile() {
        return isSendFile;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setPercentSubmitted(String percentSubmitted) {
        this.percentSubmitted = percentSubmitted;
    }

    public void setIsSendFile(boolean is) {
        this.isSendFile = is;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }
}
