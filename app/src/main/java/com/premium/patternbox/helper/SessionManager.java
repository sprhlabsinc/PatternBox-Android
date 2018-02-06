package com.premium.patternbox.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.Nullable;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "PATTERN_BOX";
	
	private static final String APP_KEY = "API_KEY";
	private static final String USER_ID = "USER_ID";
	private static final String FIRST_TIME = "FIRST_TIME";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setAPIKey(String key) {

		editor.putString(APP_KEY, key);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}
	
	public String getAPIKey(){
		return pref.getString(APP_KEY, "");
	}
	public void setUserID(int userID) {
		editor.putInt(USER_ID, userID);
		editor.commit();
	}
	public int getUserID(){
		return pref.getInt(USER_ID, 0);
	}

	public void setFirstTime(boolean firstTime) {
		editor.putBoolean(FIRST_TIME, firstTime);
		editor.commit();
	}
	public boolean getFirstTime(){
		return pref.getBoolean(FIRST_TIME, false);
	}
}
