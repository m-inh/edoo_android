package com.fries.edoo.app;

public class AppConfig {
//	public static final String URL_HOST = "http://api.uetf.me";
//	public static final String URL_HOST = "http://192.168.1.117:2344";
	public static final String URL_HOST = "http://10.0.2.2:2344";
	//Server vote comment
	public static final String URL_VOTE_COMMENT = URL_HOST + "/solve";
	//Server post comment
	public static final String URL_POST_COMMENT = URL_HOST + "/cmt";
	//Server get post and comment
	public static final String URL_GET_POST = URL_HOST + "/posts";
	//Server get LopKhoaHoc information
	public static final String URL_GET_LOPKHOAHOC = URL_HOST + "/classes";
	//Server post posts
	public static final String URL_POST_POST = URL_HOST + "/post";

	public static final String URL_POST_LIKE = URL_HOST + "/votepost";
	public static final String URL_REGISTER_FCM = URL_HOST + "/resfcm";
	public static final String URL_POST_SEEN = URL_HOST + "/seen";
	// Server user login url
	public static String URL_LOGIN = URL_HOST + "/login";
	// Server user logout url
	public static String URL_LOGOUT = URL_HOST + "/logout";
	// Server user register url
	public static String URL_REGISTER = "http://api.uetf.me/register";

	// Server user update
//	public static final String URL_UPDATE = "http://myclass.tutran.net/v1/update";
	public static final String URL_GET_USER_SOLVE_VOTE = URL_HOST + "/usersolvevote";


	//Feed
	public static final String URL_GET_FEED = "http://myclass.tutran.net/v1/feed";

	public static final String URL_GET_TKB = URL_HOST + "/timetable";

	////////////////// GCM
	public static final String TOKEN = "663812496634";
	public static final String SERVER_URL = "http://myclass.tutran.net/gcm/register";

	///////////////IMG
	public static final String URL_POST_IMG = "http://myclass.tutran.net/v1/updateAvatar";

	public static final String  URL_GET_POST_DETAIL = URL_HOST + "/post";
}
