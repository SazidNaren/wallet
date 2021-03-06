package vis.com.au.wallte.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import vis.com.au.wallte.R;

public class SubscribeActivity extends AppCompatActivity {

	Button suscribeBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_subscribe);

		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(R.drawable.back_yello);
		getSupportActionBar().setTitle("Get Premium");
        
		suscribeBtn = (Button) findViewById(R.id.suscribeButton);
		suscribeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(SubscribeActivity.this, PremiumConfirmActivity.class));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.subscribe, menu);
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
}
