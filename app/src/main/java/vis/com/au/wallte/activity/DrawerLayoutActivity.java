package vis.com.au.wallte.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import com.squareup.picasso.Picasso;
import vis.com.au.Utility.AppConstant;
import vis.com.au.activity.NotificationActivity;
import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.adapter.DrawerListAdapter;
import vis.com.au.wallte.R;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DrawerLayoutActivity extends ActionBarActivity {
    public DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Resources res;
    private ImageView userImage;
    private AppPreferences appPreferences;
    private TextView userEmailAddress, userCurrentLocation;

    private static final float MENU_POSITION = 0f;
    private static final float ARROW_POSITION = 1.0f;


    List<String> listItemsName = new ArrayList<String>();
    List<Integer> listItemsImage = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_drawer_layout);
        appPreferences = AppPreferences.getInstance(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer);
        userEmailAddress = (TextView) findViewById(R.id.userEmailAddress);
        userCurrentLocation = (TextView) findViewById(R.id.userCurrentLocation);
        userImage = (ImageView) findViewById(R.id.profile_image);
        getDetailOfUser();
        DrawerListAdapter listAdapter = new DrawerListAdapter(listItemsName, listItemsImage,appPreferences.getCountDoc());
        mDrawerList.setAdapter(listAdapter);
        listItems();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.BLACK);
            }
        }
        try {
            //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                   //host Activity
                mDrawerLayout,          //DrawerLayout object
                R.drawable.toggle_btn,   //nav drawer icon to replace 'Up' caret
                R.string.openDrawer,   //"open drawer" description
                R.string.closeDrawer   //"close drawer" description
        ) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                animateToBackArrow();
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                // animateToMenu();
            }


        };


        //set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        finish();
                        startActivity(new Intent(DrawerLayoutActivity.this, DashboardActivity.class));
                        break;
                    case 1:
                        startActivityForResult(new Intent(DrawerLayoutActivity.this, ProfileActivity.class),1001);
                        break;

                    case 2:
                        if (appPreferences.isPaidVersion()) {
                            if(appPreferences.isRepeatAlarmEnable())
                            startActivity(new Intent(DrawerLayoutActivity.this, NotificationActivity.class));
                            else
                                Toast.makeText(DrawerLayoutActivity.this, "Please enable repeat alarm in settings", Toast.LENGTH_SHORT).show();
                        }
                            else
                            startActivity(new Intent(DrawerLayoutActivity.this, SubscribeActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(DrawerLayoutActivity.this, SettingActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(DrawerLayoutActivity.this, AboutUsActivity.class));
                        break;
                   /* case 5:
                        startActivity(new Intent(DrawerLayoutActivity.this, ShareScreen.class));
                        //Toast.makeText(DrawerLayoutActivity.this, "Share clicked", Toast.LENGTH_SHORT).show();
                        break;
                   */ case 5:

                        final Dialog dialog = new Dialog(DrawerLayoutActivity.this);
                        dialog.setCancelable(false);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.exit_dialog);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                        tvMessage.setText("Do you want to logout ?");
                        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
                        tvOk.setText("Yes");
                        tvOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent logoutintent = new Intent(DrawerLayoutActivity.this, AppInfoActivity.class);
                                int PRIVATE_MODE = 0;
                                SharedPreferences loginSharedPreferences;
                                loginSharedPreferences = getSharedPreferences(AppConstant.sharedPreferenceName, PRIVATE_MODE);
                                Editor editor = loginSharedPreferences.edit();
                                editor.clear();
                                editor.commit();

                                logoutintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                logoutintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(logoutintent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
                        tvCancel.setText("No");
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                        break;

                }

            }

        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void animateToBackArrow() {
        ValueAnimator anim = ValueAnimator.ofFloat(MENU_POSITION, ARROW_POSITION);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
            }
        });

        anim.setInterpolator(new DecelerateInterpolator());
//        anim.setDuration(400);
        anim.start();

    }

    public void animateToMenu() {
        ValueAnimator anim = ValueAnimator.ofFloat(ARROW_POSITION, MENU_POSITION);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
            }
        });

        anim.setInterpolator(new DecelerateInterpolator());
//        anim.setDuration(400);
        anim.start();

    }


    public void listItems() {
        listItemsName.add("Cards, Cert. etc");
        listItemsImage.add(R.drawable.certificurt);

        listItemsName.add("Profile");
        listItemsImage.add(R.drawable.profiled);

        if (appPreferences.isPaidVersion()) {
            listItemsName.add("Notification");
            listItemsImage.add(R.drawable.notifications);
        } else {
            listItemsName.add("Get Premium");
            listItemsImage.add(R.drawable.premium);
        }

        listItemsImage.add(R.drawable.setting);
        listItemsName.add("Settings");

        listItemsImage.add(R.drawable.about);
        listItemsName.add("About us");
/*

        listItemsImage.add(R.drawable.icon_share);
        listItemsName.add("Share");
*/


        listItemsImage.add(R.drawable.logout);
        listItemsName.add("Log out");


    }

    private void getDetailOfUser() {

        try {
            SharedPreferences shared = getSharedPreferences(AppConstant.sharedPreferenceName, 0);
            JSONObject obj = new JSONObject(shared.getString("empInfo", null));
            userEmailAddress.setText(obj.getString("emp_first_name") + " " + obj.getString("emp_last_name"));
            //userCurrentLocation.setText(obj.getString(""));
            String empImage = obj.getString("emp_avatar");
            Log.e("empAvatar", empImage + "");
            Picasso.with(this).load(empImage).placeholder(R.drawable.default_profile_image).error(R.drawable.default_profile_image).into(userImage);
            String lat = obj.getString("emp_lat");
            String lng = obj.getString("emp_lang");
            userCurrentLocation.setText(appPreferences.getAddress());

            try {
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> address = gcd.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                if (address.size() > 0) {
                    userCurrentLocation.setText(address.get(0).getLocality());
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //startActivity(new Intent(DrawerLayoutActivity.this,DrawerLayoutActivity.class));
        //finish();
    }

      public void refreshDrawer()
    {
        DrawerListAdapter listAdapter = new DrawerListAdapter(listItemsName, listItemsImage,appPreferences.getCountDoc());
        mDrawerList.setAdapter(listAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1001)
        {
            startActivity(new Intent(DrawerLayoutActivity.this, DashboardActivity.class));
        }
    }
}
