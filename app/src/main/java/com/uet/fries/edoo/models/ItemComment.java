package com.uet.fries.edoo.models;

import java.io.Serializable;

/**
 * Created by TooNies1810 on 11/21/15.
 */
public class ItemComment implements Serializable {

    //name of author comment
    private String name;
    //ava of author comment
    private String avaUrl;
    private String content;
    private boolean isSolved;
    private String idComment;
    private String idAuthorComment;
    private String createAt;
    private String capability;

    public ItemComment(String idComment, String idAuthorComment, String name, String avaUrl, String content, boolean isVote, String capability) {
        this.name = name;
        this.avaUrl = avaUrl;
        this.content = content;
        this.isSolved = isVote;
        this.idComment = idComment;
        this.idAuthorComment = idAuthorComment;
        this.capability = capability;
    }

    public String getName() {
        return name;
    }

    public String getAvaUrl() {
        return avaUrl;
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

    public String getIdAuthorComment() {
        return idAuthorComment;
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

    public String getCapability(){
        return capability;
    }
}