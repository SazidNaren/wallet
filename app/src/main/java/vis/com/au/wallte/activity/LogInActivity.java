package vis.com.au.wallte.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import vis.com.au.Utility.AppConstant;
import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.helper.NetworkTask;
import vis.com.au.support.Httprequest;
import vis.com.au.wallte.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogInActivity extends Activity implements OnClickListener,NetworkTask.Result {

    EditText emailAddress, password;
    Button loginBtn;
    TextView tvBack;
    boolean isWrongIDPassword = false;
    AppPreferences appPreferences;
    NetworkTask networkTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        appPreferences = AppPreferences.getInstance(this);
        setContentView(R.layout.login);
        try {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setIcon(R.drawable.back_yello);
            getActionBar().setTitle(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginBtn = (Button) findViewById(R.id.loginBtn);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
        loginBtn.setOnClickListener(this);
        tvBack = (TextView) findViewById(R.id.tvBack);
        emailAddress.setText(appPreferences.getEmail());
        password.setText(appPreferences.getPassword());
        tvBack.setOnClickListener(this);
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvBack:
                finish();
                break;

            case R.id.loginBtn:
                final ProgressDialog pg = AppConstant.progressDialog(LogInActivity.this, "Loging...");
                pg.show();
                final String email = emailAddress.getText().toString().trim();
                if (!isValidEmail(email)) {
                    emailAddress.setError("Invalid Email");
                }
                final String pass = password.getText().toString();
                if (!isValidPassword(pass)) {
                    password.setError("Invalid Password");
                }
                try {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("email", email);
                                obj.put("password", pass);
                                obj.put("postType", AppConstant.postTypeUser);
                                String ret = Httprequest.makeHttpRequest(obj.toString(), AppConstant.logInURL);
                                String returnValue = Httprequest.retValue;
                                if (returnValue != null && !returnValue.equals("")) {
                                    final JSONObject jO = new JSONObject(returnValue);
                                    if (jO.optInt("MessageID") == 8942) {
                                        pg.dismiss();
                                        isWrongIDPassword = true;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LogInActivity.this, jO.optString("Message"), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        return;
                                    } else {
                                        String empId = jO.getString("emp_id");
                                        String isPaid = jO.optString("emp_is_paid");
                                        String firstName = jO.optString("emp_first_name");
                                        String lastName = jO.optString("emp_last_name");
                                        String lat = jO.optString("emp_lat");
                                        String lng = jO.optString("emp_lang");
                                        boolean paidStatus = isPaid.equals("1") ? true : false;
                                        appPreferences.setIsPaidVersion(paidStatus);
                                        SharedPreferences shared = getSharedPreferences(AppConstant.sharedPreferenceName, 0);
                                        Editor ed = shared.edit();
                                        ed.putString("empId", empId);
                                        appPreferences.setEmp_id(empId);
                                        Log.e("emp_id", empId + "");
                                        ed.putBoolean("hasRun", true);
                                        appPreferences.setEmail(email);
                                        appPreferences.setPassword(pass);
                                        appPreferences.setAddress(jO.optString("emp_address"));
                                        ed.putString("empInfo", jO.toString());
                                        ed.commit();
                                        isWrongIDPassword = false;
                                        Intent intent = new Intent(LogInActivity.this, DashboardActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        pg.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                pg.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    if (isWrongIDPassword) {
                       // Toast.makeText(LogInActivity.this, "wrong email id or password", Toast.LENGTH_SHORT).show();
                        // pg.dismiss();
                    }
                } catch (Exception e) {
                    pg.dismiss();
                    e.printStackTrace();
                }
               // hitAllDataApi();
                break;

        }

    }

    private void hitAllDataApi() {
        final String email = emailAddress.getText().toString().trim();
        if (!isValidEmail(email)) {
            emailAddress.setError("Invalid Email");
        }
        final String pass = password.getText().toString();
        if (!isValidPassword(pass)) {
            password.setError("Invalid Password");
        }
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", email);
            obj.put("password", pass);
            obj.put("postType", AppConstant.postTypeUser);

            List<NameValuePair> listValue = new ArrayList<NameValuePair>();
            listValue.add(new BasicNameValuePair("email", email));
            listValue.add(new BasicNameValuePair("password", pass));
            listValue.add(new BasicNameValuePair("postType", AppConstant.postTypeUser));
            networkTask = new NetworkTask(LogInActivity.this, 1011, obj.toString());
            networkTask.exposePostExecute(this);
            networkTask.execute(AppConstant.logInURL);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 2) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    public void resultFromNetwork(String returnValue, int id, Object arg1, Object arg2) {
        try {
            if (returnValue != null && !returnValue.equals("")) {
                JSONObject jO = new JSONObject(returnValue);
                if (jO.optInt("MessageID") == 8942) {
                    Toast.makeText(LogInActivity.this, jO.optString("Message"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String empId = jO.getString("emp_id");
                    String isPaid = jO.optString("emp_is_paid");
                    String firstName = jO.optString("emp_first_name");
                    String lastName = jO.optString("emp_last_name");
                    String lat = jO.optString("emp_lat");
                    String lng = jO.optString("emp_lang");
                    boolean paidStatus = isPaid.equals("1") ? true : false;
                    appPreferences.setIsPaidVersion(paidStatus);
                    SharedPreferences shared = getSharedPreferences(AppConstant.sharedPreferenceName, 0);
                    Editor ed = shared.edit();
                    ed.putString("empId", empId);
                    appPreferences.setEmp_id(empId);
                    Log.e("emp_id", empId + "");
                    ed.putBoolean("hasRun", true);
                    appPreferences.setEmail(emailAddress.getText().toString().trim());
                    appPreferences.setPassword(password.getText().toString().trim());
                    ed.putString("empInfo", jO.toString());
                    ed.commit();
                    isWrongIDPassword = false;
                    Intent intent = new Intent(LogInActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}