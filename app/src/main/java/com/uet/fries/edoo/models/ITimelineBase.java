package com.uet.fries.edoo.models;

import android.text.format.DateFormat;
import android.util.Log;

import com.uet.fries.edoo.utils.CommonVLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by tmq on 13/10/2016.
 */

public class ITimelineBase implements Serializable {

    public static final String TYPE_POST_QUESTION = "question";
    public static final String TYPE_POST_NOTE = "note";
    public static final String TYPE_POST_NOTIFICATION = "notification";
    public static final String TYPE_POST_POLL = "poll";
    public static final String TYPE_POST_EXERCISE = "event";

    // Post
    private String title;
    private String content;
    private String summary;
    private String type;
    private String idPost;
    private boolean isSeen;
    private String createAt;
    private int commentCount;
    private ArrayList<ItemComment> itemComments = new ArrayList<>();

    private String keyLopType;

    // Author
    private String nameAuthor;
    private String avaAuthor;
    private String typeAuthor;
    private String idAuthor;

    public ITimelineBase() {
    }

    public ITimelineBase(JSONObject jsonTimeline) throws JSONException {
        this.title = jsonTimeline.getString("title");
        this.content = jsonTimeline.getString("content");
        this.summary = jsonTimeline.getString("description");
        this.type = jsonTimeline.getString("type");
        this.idPost = jsonTimeline.getString("id");
        this.commentCount = jsonTimeline.getInt("comment_count");

        this.createAt = jsonTimeline.getString("created_at");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(CommonVLs.TIME_FORMAT);
            this.createAt = DateFormat.format("dd/MM/yy", sdf.parse(this.createAt).getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {   // Khi vao PostDetails thi khong co thuoc tinh is_seen
            this.isSeen = jsonTimeline.getInt("is_seen") == 1;
        }catch (JSONException e){
            e.printStackTrace();
        }

        //author post
        this.nameAuthor = "Ẩn danh";
        this.avaAuthor = "okmen.com";
        this.idAuthor = "";
        this.typeAuthor = "";

        try {   // Neu bai viet an danh thi khong co author
            JSONObject jsonAuthorPost = jsonTimeline.getJSONObject("author");
            this.nameAuthor = jsonAuthorPost.getString("name");
            this.avaAuthor = jsonAuthorPost.getString("avatar");
            this.idAuthor = jsonAuthorPost.getString("id");
            this.typeAuthor = jsonAuthorPost.getString("capability");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public String getAvaAuthor() {
        return avaAuthor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<ItemComment> getItemComments() {
        return itemComments;
    }

    public void setItemComments(ArrayList<ItemComment> itemComments) {
        this.itemComments = itemComments;
    }

    public void deleteComment(String idComment) {
        for (int i = 0; i < itemComments.size(); i++) {
            if (idComment.equalsIgnoreCase(itemComments.get(i).getIdComment())) {
                itemComments.remove(i);
                return;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getKeyLopType() {
        return keyLopType;
    }

    public void setKeyLopType(String keyLopType) {
        this.keyLopType = keyLopType;
    }

    public String getTypeAuthor() {
        return typeAuthor;
    }

    public void setTypeAuthor(String typeAuthor) {
        this.typeAuthor = typeAuthor;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
