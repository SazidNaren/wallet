package vis.com.au.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class AppConstant {
	
	public static String postTypeUser = "1";//employee
	public static String sharedPreferenceName = "walletEmployee";
	public static String mainUrl = "http://workerswallet.com.au/walletapi/";
	public static String logInURL = "func_login.php";
	public static String signUpURL = "func_signup.php";
	public static String upLoadData = "uploadDoc.php";
	public static String empProfile = "edit_profile.php";
	public static String reSetPass = "edit_password.php";
	public static String editDocument = "Edit_document.php";
	public static String uploadDocument = mainUrl+"uploadDoc.php";
	public static String updateToPaidVersion = mainUrl+"function.php";
	//http://workerswallet.com.au/walletapi/random.php
	public static String promoCode = mainUrl+"random.php";

	public static void showToast(final Activity ac,final String text)
	{
		ac.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ac.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public static void EdittextError(EditText editText, String Error)  {
		  editText.setError(Error);
		  editText.requestFocus();  }
	

	public static ProgressDialog progressDialog(Context context,String text){
		ProgressDialog pg=new ProgressDialog(context);
		pg.setMessage(text);
		pg.setCancelable(false);
		pg.setCanceledOnTouchOutside(false);
		pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		return pg;
	}
	
}
