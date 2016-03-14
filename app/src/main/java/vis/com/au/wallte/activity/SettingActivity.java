package vis.com.au.wallte.activity;

import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.wallte.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity implements OnClickListener {
	private RelativeLayout settingRelativeLayout;
	private LinearLayout notification_setting_layout;
	private RelativeLayout manage_relativelayout;
	private AppPreferences appPreferences;
	private ToggleButton repeatAlarm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(R.drawable.back_btn2);
		getSupportActionBar().setTitle("Setting");
		getSupportActionBar().setTitle("Setting");
		appPreferences=AppPreferences.getInstance(this);
		init();
		settingRelativeLayout = (RelativeLayout) findViewById(R.id.settingRelativeLayout);
		notification_setting_layout.setOnClickListener(this);
		manage_relativelayout.setOnClickListener(this);
		repeatAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				appPreferences.setRepeatAlarmEnable(isChecked);
			}
		});
		settingRelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,ChangePasswordActivity.class));
			}
		});
		userStatusSettings();
	}

	private void init() {
		notification_setting_layout=(LinearLayout)findViewById(R.id.notification_setting_layout);
		manage_relativelayout=(RelativeLayout)findViewById(R.id.manage_relativelayout);
		repeatAlarm=(ToggleButton)findViewById(R.id.repeat_alarm);
	}

	private void userStatusSettings() {
		if(!appPreferences.isPaidVersion())
		{
			notification_setting_layout.setVisibility(View.GONE);
			manage_relativelayout.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		switch (id)
		{
			case R.id.notification_setting_layout:

				break;
			case R.id.manage_relativelayout:
				startActivity(new Intent(SettingActivity.this,ManageFolderActivity.class));
				break;
		}
	}
}
