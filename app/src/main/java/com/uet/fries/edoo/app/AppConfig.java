package com.uet.fries.edoo.app;

public class AppConfig {
//	public static final String URL_HOST = "http://api-v2.uetf.me";
//	public static final String URL_HOST = "http://api.uetf.me";
//	public static final String URL_HOST = "http://10.0.2.2:2344";
	public static final String URL_HOST = "https://edoo.vn/api/";
	//Server vote comment
	public static final String URL_SOLVE_COMMENT = URL_HOST + "/solve";
	public static final String URL_UNSOLVE_COMMENT = URL_HOST + "/unsolve";
	public static final String URL_DELETE_COMMENT = URL_HOST + "/deletecmt";

	//Server post comment
	public static final String URL_POST_COMMENT = URL_HOST + "/cmt";
	//Server get post and comment
	public static final String URL_GET_POST = URL_HOST + "/posts";
	//Server get LopKhoaHoc information
	public static final String URL_GET_LOPKHOAHOC = URL_HOST + "/classes";
	//Server post posts
	public static final String URL_POST_POST = URL_HOST + "/post";
	public static final String URL_EDIT_POST = URL_HOST + "/updatepost";

	public static final String URL_POST_LIKE = URL_HOST + "/votepost";
	public static final String URL_REGISTER_FCM = URL_HOST + "/resfcm";
	public static final String URL_POST_SEEN = URL_HOST + "/seen";

	///posts/{class_id}/page/{page_number}
	public static final String URL_GET_POST_IN_PAGE = URL_HOST + "/posts";

	// Server user login url
	public static String URL_LOGIN = URL_HOST + "/login";
	// Server user logout url
	public static String URL_LOGOUT = URL_HOST + "/logout";
	// Reset password
	public static String URL_RESET_PASSWORD = URL_HOST + "/resetpass";


	public static final String URL_GET_USER_SOLVE_VOTE = URL_HOST + "/usersolvevote";

	public static final String URL_GET_TKB = URL_HOST + "/timetable";

	///////////////IMG
	public static final String URL_POST_IMG_AVATAR = URL_HOST + "/avatar";
	public static final String URL_POST_IMG = URL_HOST + "/img";


	public static final String URL_GET_PROFILE = URL_HOST + "/profile";
	public static final String URL_POST_CHANGE_PASS = URL_HOST + "/changepass";



	public static final String  URL_GET_POST_DETAIL = URL_HOST + "/post";
	public static final String  URL_DELETE_POST = URL_HOST + "/deletepost";


	// Event
	public static final String  URL_UPFILE_EVENT = URL_HOST + "/upfileevent/";
	public static final String  URL_CHECK_EVENT = URL_HOST + "/checkevent/";

}
