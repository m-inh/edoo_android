package com.fries.edoo.app;

public class AppConfig {
	public static final String URL_HOST = "http://api.uetf.me";
	//Server vote comment
	public static final String URL_VOTE_COMMENT = "http://myclass.tutran.net/v1/vote";
	//Server post comment
	public static final String URL_POST_COMMENT = "http://myclass.tutran.net/v1/post/comment";
	//Server get post and comment
	public static final String URL_GET_POST = "http://myclass.tutran.net/v1/getPosts";
	//Server get LopKhoaHoc information
	public static final String URL_GET_LOPKHOAHOC = "http://myclass.tutran.net/v1/getGroup";
	//Server post posts
	public static final String URL_POST_POST = "http://myclass.tutran.net/v1/post";

	public static final String URL_POST_LIKE = "http://myclass.tutran.net/v1/like";

	public static final String URL_POST_DISLIKE = "http://myclass.tutran.net/v1/dislike";
	// Server user login url
	public static String URL_LOGIN = URL_HOST + "/login";
	// Server user logout url
	public static String URL_LOGOUT = URL_HOST + "/logout";
	// Server user register url
	public static String URL_REGISTER = "http://api.uetf.me/register";
	// Server user update
	public static final String URL_UPDATE = "http://myclass.tutran.net/v1/update";

	//Feed
	public static final String URL_GET_FEED = "http://myclass.tutran.net/v1/feed";

	public static final String URL_GET_TKB = "http://myclass.tutran.net/v1/timetable";

	////////////////// GCM
	public static final String TOKEN = "663812496634";
	public static final String SERVER_URL = "http://myclass.tutran.net/gcm/register";

	///////////////IMG
	public static final String URL_POST_IMG = "http://myclass.tutran.net/v1/updateAvatar";
}
