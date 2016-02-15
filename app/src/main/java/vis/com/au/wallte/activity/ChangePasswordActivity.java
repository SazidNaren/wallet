package vis.com.au.wallte.activity;

import org.json.JSONException;
import org.json.JSONObject;

import vis.com.au.Utility.AppText;
import vis.com.au.Utility.CircleImageView;
import vis.com.au.support.Httprequest;
import vis.com.au.wallte.R;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;

    private Button sendButton;
    private String oldPin;
    private String newPin;
    private String confirmPin;
    private CircleImageView profilePic;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        getSupportActionBar().setTitle("Change Password");

        currentPasswordEditText = (EditText) findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        profilePic = (CircleImageView) findViewById(R.id.profile_image);

        SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(shared.getString("empInfo", null));
            tvEmail.setText(jObj.getString("emp_email"));
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            });
            String empImage = jObj.getString("emp_avatar");
            builder.build().load(empImage).placeholder(R.drawable.suscribe).error(R.drawable.sss_error).into(profilePic);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("returnFirst", jObj + "");


        sendButton = (Button) findViewById(R.id.saveBtn);

        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (currentPasswordEditText.getText().toString().equalsIgnoreCase(newPasswordEditText.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Current password and new password cannot be same", Toast.LENGTH_SHORT).show();
                } else if (!newPasswordEditText.getText().toString().equalsIgnoreCase(currentPasswordEditText.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Current password and confirm password must be same", Toast.LENGTH_SHORT).show();

                } else {
                    final ProgressDialog pg = AppText.progressDialog(ChangePasswordActivity.this, "Resetting password...");
                    pg.show();
                    checkValidation();
                    try {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                JSONObject jobj = new JSONObject();
                                try {
                                    jobj.put("postType", AppText.postTypeUser);
                                    jobj.put("userId", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("empId", null));
                                    jobj.put("pwd", oldPin);
                                    jobj.put("rePwd", newPin);

                                    Httprequest.makeHttpRequest(jobj.toString(), AppText.reSetPass);
                                    Log.e("sendToserverReSetPass", jobj + "");
                                    String returnMessage = Httprequest.retValue;
                                    Log.e("returnMessage", returnMessage + "");
                                    AppText.showToast(ChangePasswordActivity.this, "Password is successfully reset");
                                    pg.dismiss();
                                } catch (JSONException e) {
                                    pg.dismiss();
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        pg.dismiss();
                        e.printStackTrace();
                    }
                    //pg.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.change_password, menu);
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

    private boolean checkValidation() {
        boolean valid = true;
        oldPin = currentPasswordEditText.getText().toString();
        newPin = newPasswordEditText.getText().toString();
        confirmPin = confirmPasswordEditText.getText().toString();

        if (oldPin.length() < 1 || oldPin.equals("")) {
            AppText.EdittextError(newPasswordEditText, "emptyField");
            valid = false;
        }
        //check if password empty
        if (newPin.equals("") || newPin.length() < 6 || newPin.length() > 10) {
            AppText.EdittextError(newPasswordEditText, "between 6 and 10 alphanumeric characters");
            valid = false;
        }
        //check the passwords
        if (!newPin.equals(confirmPin)) {
            AppText.EdittextError(confirmPasswordEditText, "Password Mismatch");
            valid = false;
        }

        return valid;
    }
}
