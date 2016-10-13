package com.uet.fries.edoo.models;

import com.uet.fries.edoo.utils.CommonVLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by TooNies1810 on 11/21/15.
 */
public class ItemComment implements Serializable {

    //Author comment
    private String nameAuthor;
    private String capabilityAuthor;
    private String avaUrlAuthor;
    private String idAuthor;

    // Comment info
    private String content;
    private boolean isSolved;
    private String idComment;
    private String createAt;

    public ItemComment(String idComment, String idAuthorComment, String name, String avaUrl, String content, boolean isVote, String capability) {
        this.nameAuthor = name;
        this.avaUrlAuthor = avaUrl;
        this.content = content;
        this.isSolved = isVote;
        this.idComment = idComment;
        this.idAuthor = idAuthorComment;
        this.capabilityAuthor = capability;
    }

    public ItemComment(JSONObject jsonCmt) throws JSONException{
        this.idComment = jsonCmt.getString("id");
        this.content = jsonCmt.getString("content");
        this.createAt = CommonVLs.convertDate(jsonCmt.getString("created_at"));
        this.isSolved = jsonCmt.getInt("is_solve") == 1;

        this.nameAuthor = "";
        this.idAuthor = "";
        this.capabilityAuthor = "";
        this.avaUrlAuthor = "";

        try {
            JSONObject jsonAuthorComment = jsonCmt.getJSONObject("author");
            this.nameAuthor = jsonAuthorComment.getString("name");
            this.idAuthor = jsonAuthorComment.getString("id");
            this.capabilityAuthor = jsonAuthorComment.getString("capability");
            this.avaUrlAuthor = jsonAuthorComment.getString("avatar");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public String getAvaUrlAuthor() {
        return avaUrlAuthor;
    }

    public String getContent() {
        return content;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public String getIdComment() {
        return idComment;
    }

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIsSolved(boolean solved) {
        isSolved = solved;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getCapabilityAuthor(){
        return capabilityAuthor;
    }
}
