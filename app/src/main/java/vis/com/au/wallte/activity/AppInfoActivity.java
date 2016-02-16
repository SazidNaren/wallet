package vis.com.au.wallte.activity;

import vis.com.au.Utility.AppText;
import vis.com.au.adapter.ViewPagerAdater;
import vis.com.au.wallte.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

//import com.viewpagerindicator.CirclePageIndicator;

public class AppInfoActivity extends Activity {

    ViewPager viewPager;
    ViewPagerAdater adapter;
    int[] image;
    //CirclePageIndicator cIndicator;
    Button loginBtn, createAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.app_info);
        //SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
        /*String checkUserId = shared.getString("emp_id",-1+"");
		if(checkUserId != null){
			startActivity(new Intent(AppInfoActivity.this,DashboardActivity.class));
		}*/
        /*try {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
            getActionBar().setTitle(null);
        } catch (Exception e) {
            e.printStackTrace();

        }*/
        if (getSharedPreferences(AppText.sharedPreferenceName, 0).getString("empId", null) != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();

        }
        image = new int[]
                {
                        R.drawable.login_info, R.drawable.scan_field, R.drawable.managefolder_info, R.drawable.subscribe_info
                };


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPagerAdater(AppInfoActivity.this, image);
        viewPager.setAdapter(adapter);
        //	cIndicator=(CirclePageIndicator) findViewById(R.id.circularPage);
        //	cIndicator.setViewPager(viewPager);

        loginBtn = (Button) findViewById(R.id.logIn);
        loginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(i);


            }
        });

        createAccountBtn = (Button) findViewById(R.id.createAccount);
        createAccountBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), RegistrationScreen.class);
                startActivity(i);
            }
        });

    }
}
