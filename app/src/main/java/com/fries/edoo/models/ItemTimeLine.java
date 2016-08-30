package com.fries.edoo.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TooNies1810 on 11/20/15.
 */
public class ItemTimeLine extends ItemBase implements Serializable {
    private static final String TAG = ItemTimeLine.class.getSimpleName();
    public static final String TYPE_POST_QUESTION = "question";
    public static final String TYPE_POST_NOTE = "note";
    public static final String TYPE_POST_NOTIFICATION = "notification";
    public static final String TYPE_POST_POLL = "poll";
    private String title;

    //name of author post
    private String type;
    private String idPost;
    private String name;
    private String ava;
    private String typeAuthor;
    private String idAuthor;
    private String content;
    private String description;
    private String keyLopType;
    private int like;
    private boolean isConfirmByTeacher;
    private boolean isSeen;
    private boolean isSolve;
    private String createAt;
    private int commentCount;
    private ArrayList<ItemComment> itemComments = new ArrayList<>();

    public ItemTimeLine(String idPost, String title, String name, String ava, String content, int like, boolean isConfirmByTeacher, String type) {
        this.name = name;
        this.idPost = idPost;
        this.title = title;
        this.ava = ava;
        this.content = content;
        this.like = like;
        this.isConfirmByTeacher = isConfirmByTeacher;
        this.type = type;

//        Log.i(TAG, "name: " + name);
//        Log.i(TAG, "title: " + title);
//        Log.i(TAG, "ava: " + ava);
//        Log.i(TAG, "content: " + content);
//        Log.i(TAG, "like: " + like);
//        Log.i(TAG, "is confirmed: " + isConfirmByTeacher);
    }

    public String getName() {
        return name;
    }

    public String getAva() {
        return ava;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public boolean isConfirmByTeacher() {
        return isConfirmByTeacher;
    }

    public void setIsConfirmByTeacher(boolean isConfirmByTeacher) {
        this.isConfirmByTeacher = isConfirmByTeacher;
    }

    public ArrayList<ItemComment> getItemComments() {
        return itemComments;
    }

    public void setItemComments(ArrayList<ItemComment> itemComments) {
        this.itemComments = itemComments;
    }

    public void deleteComment(String idComment){
        for (int i=0; i<itemComments.size(); i++){
            if (idComment.equalsIgnoreCase(itemComments.get(i).getIdComment())){
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

    public boolean isSolve() {
        return isSolve;
    }

    public void setSolve(boolean solve) {
        isSolve = solve;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
