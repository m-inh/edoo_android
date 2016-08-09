package com.fries.edoo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = "SessionManager";

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "AndroidHiveLogin";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	private static final String KEY_TOKEN_LOG_IN = "tokenLogin";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setLogin(boolean isLoggedIn) {
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

		// Delete token
		if (!isLoggedIn) editor.remove(KEY_TOKEN_LOG_IN);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}

	public void setTokenLogin(String token) {
		editor.putString(KEY_TOKEN_LOG_IN, token);

		// commit changes
		editor.commit();

		Log.d(TAG, "Saved token: " + token);
	}
	
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}

	public String getTokenLogin() {
		String token = "";
		try {
			token = pref.getString(KEY_TOKEN_LOG_IN, "none");
		} catch (Exception e) {
			Log.e(TAG, "Token error");
		}

		return token;
	}
}
