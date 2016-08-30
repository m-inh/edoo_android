package com.fries.edoo.utils;

/**
 * Created by tmq on 30/08/2016.
 */
public class PermissonManager {

    public static boolean pDeletePost(String authorPostId, String authorPostType, String userId, String userType) {
        boolean userIsTeacher = userType.equalsIgnoreCase("teacher");
        boolean authorIsTeacher = authorPostType.equalsIgnoreCase("teacher");
        boolean userIsAuthor = userId.equalsIgnoreCase(authorPostId);
        if (userIsTeacher) {
            return true;
        }

        // User is Student ...
        if (authorIsTeacher) {
            return false;
        }
        if (userIsAuthor) {
            return true;
        }
        return false;
    }

    public static boolean pDeleteComment(String authorPostId, String authorCommentType, String userId, String userType) {
        boolean userIsTeacher = userType.equalsIgnoreCase("teacher");
        boolean authorCommentIsTeacher = authorCommentType.equalsIgnoreCase("teacher");
        boolean userIsAuthorPost = userId.equalsIgnoreCase(authorPostId);

        if (userIsTeacher) {
            return true;
        }

        // User is student
        if (userIsAuthorPost && !authorCommentIsTeacher) {
            return true;
        }
        return false;
    }

    public static boolean pSolveComment(String authorPostId, String userId, String userType, String authorCommentId) {
        boolean userIsTeacher = userType.equalsIgnoreCase("teacher");
        boolean userIsAuthorPost = userId.equalsIgnoreCase(authorPostId);
        boolean userIsAuthorComment = userId.equalsIgnoreCase(authorCommentId);
        boolean authorCommentIsAuthorPost = authorCommentId.equalsIgnoreCase(authorPostId);

        if (userIsAuthorComment){
            return false;
        }

        if (authorCommentIsAuthorPost) {
            return false;
        }

        if (userIsAuthorPost) {
            return true;
        }

        if (userIsTeacher){
            return true;
        }

        return false;
    }

}
