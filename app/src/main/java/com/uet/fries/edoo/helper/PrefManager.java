package com.uet.fries.edoo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class PrefManager {
	// LogCat tag
	private static String TAG = PrefManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "edoo_session";
	
	private static final String KEY_IS_LOGGED_IN = "is_login";
	private static final String KEY_IS_FIRST_LOGGED_IN = "is_first_login";
	private static final String KEY_TOKEN_LOG_IN = "token_login";
	private static final String KEY_IS_SAVE_CLASS = "is_save_class";

	public PrefManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setIsFirstLogin(boolean isFirstLogin) {
		editor.putBoolean(KEY_IS_FIRST_LOGGED_IN, isFirstLogin);

		// Delete token
//		if (!isLoggedIn) editor.remove(KEY_TOKEN_LOG_IN);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login first login modified!");
	}

	public void setLogin(boolean isLoggedIn) {
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

		// Delete token
//		if (!isLoggedIn) editor.remove(KEY_TOKEN_LOG_IN);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}

	public void setTokenLogin(String token) {
		editor.putString(KEY_TOKEN_LOG_IN, token);

		// commit changes
		editor.commit();

//		Log.d(TAG, "Saved token: " + token);
	}

	public boolean isFirstLoggedIn(){
		return pref.getBoolean(KEY_IS_FIRST_LOGGED_IN, true);
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

	// ---------------------------------------------------------------------------------------------

	public void setIsSaveClass(boolean isSave) {
		editor.putBoolean(KEY_IS_SAVE_CLASS, isSave);
		editor.commit();
		Log.d(TAG, "Is Save class: " + isSave);
	}

	public boolean isSaveClass(){
		return pref.getBoolean(KEY_IS_SAVE_CLASS, false);
	}
}
