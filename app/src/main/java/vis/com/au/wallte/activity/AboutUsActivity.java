package vis.com.au.wallte.activity;

import vis.com.au.wallte.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutUsActivity extends AppCompatActivity {

	WebView mainWebview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		//getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(R.drawable.back_white);
		getSupportActionBar().setTitle("About Us");
		
		try {

			String webLink = "http://workerswallet.com.au/walletapi/wallet/AboutUs.html";
			mainWebview = (WebView) findViewById(R.id.theMainWebView);
			mainWebview.setOverScrollMode(View.OVER_SCROLL_NEVER);
			mainWebview.setHorizontalScrollBarEnabled(true);
			mainWebview.getSettings().setJavaScriptEnabled(true);
			mainWebview.loadUrl(webLink);
			WebClientClass webViewClient = new WebClientClass();
			mainWebview.setWebViewClient(webViewClient);
			mainWebview.setWebChromeClient(new WebChromeClient());
			mainWebview.setVerticalScrollBarEnabled(true);
			mainWebview.requestFocus(View.FOCUS_DOWN);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_us, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class WebClientClass extends WebViewClient {
		  ProgressDialog pd = null;

		  @Override
		  public void onPageStarted(WebView view, String url, Bitmap favicon) {
		   super.onPageStarted(view, url, favicon);
		   pd = new ProgressDialog(AboutUsActivity.this);
		   pd.setTitle("Please wait");
		   pd.setMessage("Page is loading..");
		   pd.show();
		  }

		  @Override
		  public void onPageFinished(WebView view, String url) {
		   super.onPageFinished(view, url);
		   pd.dismiss();
		  }
		 }
}
