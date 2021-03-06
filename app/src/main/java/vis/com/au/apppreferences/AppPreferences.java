package vis.com.au.apppreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * This class is used to save data 
 */
public class AppPreferences {
	private static AppPreferences mPreferences = null;
	// private Context mContext = null;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private String email,emp_id,password;

	public String getPassword() {
		 return sharedPreferences.getString("password", "");
	}

	public void setPassword(String password) {
		editor.putString("password", password);
		editor.commit();
	}

	private boolean isPaidVersion;

	public boolean isPaidVersion() {
		return sharedPreferences.getBoolean("is_paid_version", false);
	}

	public void setIsPaidVersion(boolean isPaidVersion) {
		editor.putBoolean("is_paid_version", isPaidVersion);
		editor.commit();
	}
	public boolean isRepeatAlarmEnable() {
		return sharedPreferences.getBoolean("is_repeat_alarm", false);
	}

	public void setRepeatAlarmEnable(boolean isRepeatAlarm) {
		editor.putBoolean("is_repeat_alarm", isRepeatAlarm);
		editor.commit();
	}

	public String getEmp_id() {
		return sharedPreferences.getString("employee_id", "");
	}

	public void setEmp_id(String emp_id) {
		editor.putString("employee_id", emp_id);
		editor.commit();
	}

	public String getCountDoc() {
		return sharedPreferences.getString("countDoc", "");
	}

	public void setCountDoc(String email) {
		editor.putString("countDoc", email);
		editor.commit();
	}
	public String getAddress() {
		return sharedPreferences.getString("address", "");
	}

	public void setAddress(String email) {
		editor.putString("address", email);
		editor.commit();
	}
	public String getEmail() {
		return sharedPreferences.getString("email_id", "");
	}

	public void setEmail(String email) {
		editor.putString("email_id", email);
		editor.commit();
	}
	private AppPreferences(Context nContext) {
		// this.mContext = nContext;
		this.sharedPreferences = nContext.getSharedPreferences(
				"wallet_preference", Context.MODE_PRIVATE);
		this.editor = sharedPreferences.edit();
	}

	public static synchronized AppPreferences getInstance(Context nContext) {
		if (mPreferences == null) {
			mPreferences = new AppPreferences(nContext);
		}
		return mPreferences;
	}

	public Editor getEditor() {
		return editor;
	}








}