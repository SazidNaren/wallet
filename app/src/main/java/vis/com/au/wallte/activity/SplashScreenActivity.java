package vis.com.au.wallte.activity;

import vis.com.au.wallte.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_screen);
		
		final String PREFS_NAME = "hasRunBefore";
		
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {  
					int waited = 0;
					while (waited < 1000) {
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					finish();
					
					SharedPreferences runCheck = getSharedPreferences(PREFS_NAME, 0); //load the prefereces
					Boolean hasRun = runCheck.getBoolean("hasRun", false); //see if its run before. The default is NO.

						if (!hasRun) {
						 //apply
						    //code for if this is the first time the app has run
						    
						    Log.i(this.getClass().getName(), "RUN for the first time");
						    
						  //the starter activity to show USUALLY!!
							Intent initialMsewaRegistrationIntent = new Intent();
							initialMsewaRegistrationIntent.setClass(getApplicationContext(), AppInfoActivity.class);					
							startActivity(initialMsewaRegistrationIntent);
							finish();
						    
						}
						
						else {
							
							Log.i(this.getClass().getName(), "RUN usually!!");
							
							//the starter activity to show USUALLY!!
							Intent homeListIntent = new Intent();
							homeListIntent.setClass(getApplicationContext(), DashboardActivity.class);
												
							startActivity(homeListIntent);
							finish();
						}
					
				}
			}
		};
		
		splashThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
