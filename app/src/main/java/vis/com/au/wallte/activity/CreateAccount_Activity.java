package vis.com.au.wallte.activity;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import vis.com.au.Utility.AppText;
import vis.com.au.Utility.MyLocation;
import vis.com.au.Utility.MyLocation.LocationResult;
import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.support.Httprequest;
import vis.com.au.wallte.R;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccount_Activity extends ActionBarActivity implements OnClickListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private String fileName;
    LocationManager lManager;
    MyLocation myLocation = new MyLocation();
    private ImageView imgView;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText firstNameEdt, lastNameEdt, DboEdt, mobileEdt, addressEdt, emailAddressEditText, userNameEdt, passwordEdt, confirmPassEdt;
    private TextView notificationTextViewImage;
    Toolbar toolBar;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        appPreferences = AppPreferences.getInstance(this);
        initiallizeUIE();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setDateField();
        Log.e("LocationResult", locationResult + "");
        myLocation.getLocation(getApplicationContext(), locationResult);
        //boolean r = myLocation.getLocation(getApplicationContext(), locationResult);
    }

	/*@Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
		return super.onPrepareOptionsPanel(view, menu);
	}*/

    private void initiallizeUIE() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        getSupportActionBar().setTitle("Registration");


        /*toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);*/
        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar.setTitle("Create Account");*/


        imgView = (ImageView) findViewById(R.id.profile_image);
        imgView.setOnClickListener(this);

        firstNameEdt = (EditText) findViewById(R.id.firstNameEdt);
        lastNameEdt = (EditText) findViewById(R.id.lastNameEdt);
        DboEdt = (EditText) findViewById(R.id.DboEdt);
        mobileEdt = (EditText) findViewById(R.id.mobileEdt);
        addressEdt = (EditText) findViewById(R.id.addressEdt);
        emailAddressEditText = (EditText) findViewById(R.id.emailAddressEditText);
        userNameEdt = (EditText) findViewById(R.id.userNameEdt);
        passwordEdt = (EditText) findViewById(R.id.passwordEdt);
        confirmPassEdt = (EditText) findViewById(R.id.confirmPassEdt);
        notificationTextViewImage = (TextView) findViewById(R.id.notificationTextViewImage);

    }

    private void sendDateToServer() {
        String emailUser = emailAddressEditText.getText().toString();
        String passwordUser = passwordEdt.getText().toString();
        //String retypePassword = confirmPassEdt.getText().toString();
        String firstname = firstNameEdt.getText().toString();
        String lastname = lastNameEdt.getText().toString();

        if (!checkValidation()) {
            onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(CreateAccount_Activity.this);
        //progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
        try {

            final JSONObject obj = new JSONObject();
            obj.put("postType", AppText.postTypeUser);
            obj.put("userName", userNameEdt.getText().toString());
            obj.put("fName", firstname);
            obj.put("lName", lastname);
            obj.put("emailId", emailUser);//emailAddressEditText.getText().toString().trim()
            obj.put("DOB", DboEdt.getText().toString());
            obj.put("Address", addressEdt.getText().toString());
            obj.put("mobile", mobileEdt.getText().toString());
            obj.put("password", passwordUser);
            obj.put("isSubscribed", 0);
            obj.put("deviceType", "android");
            obj.put("deviceID", android.provider.Settings.Secure.getString(getContentResolver(), "android_id"));
            obj.put("lat", shared.getString("Latitude", ""));
            obj.put("lang", shared.getString("Longitude", ""));
            obj.put("avatar", fileName);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {

                        String returnValue = Httprequest.makeHttpRequest(obj.toString(), AppText.signUpURL);
                        Log.e("DataSend", obj.toString());
                        progressDialog.dismiss();
                        AppText.showToast(CreateAccount_Activity.this, "Account has been Created");
                        Intent i = new Intent(CreateAccount_Activity.this, AppInfoActivity.class);
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && data != null) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgPath = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                // Get the Image's file name
                //  String fileNameSegments[] = imgPath.split("/");
                // fileName = fileNameSegments[fileNameSegments.length - 1];
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                fileName = Base64.encodeToString(byte_arr, 0);
                SharedPreferences shard = getSharedPreferences(AppText.sharedPreferenceName, 0);
                Editor edit = shard.edit();
                edit.putString("oldImage", fileName);
                edit.commit();
                Log.e("fileName", fileName + "");
                // Put file name in Async Http Post Param which will used in Php web app

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.profile_image:
                loadImagefromGallery(v);
                break;

            case R.id.DboEdt:
                fromDatePickerDialog.show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reqistration_save_btn, menu);*/
        getMenuInflater().inflate(R.menu.reqistration_save_btn, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionSaveReqiatrationBtn:
                sendDateToServer();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkValidation() {
        boolean valid = true;
        String emailUser = emailAddressEditText.getText().toString();
        String paswordUser = passwordEdt.getText().toString();
        String retypePassword = confirmPassEdt.getText().toString();
        String firstname = firstNameEdt.getText().toString();
        String lastname = lastNameEdt.getText().toString();

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailUser;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (!matcher.matches() || emailUser.equals("")) {
            AppText.EdittextError(emailAddressEditText, "enter a valid email address");
            valid = false;
        }
        //check if password empty
        if (paswordUser.equals("")) {
            AppText.EdittextError(passwordEdt, "Password can't be empty");
            valid = false;
        }
        //check the passwords
        if (!paswordUser.equals(retypePassword)) {
            AppText.EdittextError(confirmPassEdt, "Password Mismatch");
            valid = false;
        }
        //check if firsts name empty
        if (firstname.isEmpty() || firstname.length() < 3) {
            AppText.EdittextError(firstNameEdt, "at least 3 characters");
            valid = false;
        }
        //check if last name empty
        if (lastname.equals("")) {
            AppText.EdittextError(lastNameEdt, "Pleae Enter Last Name");
            valid = false;
        }
        //check image is slected or not
       /* if (imgView.getDrawable() == null) {
            AppText.EdittextError(notificationTextViewImage, "Pleae select image");
            valid = false;
        }*/
        return valid;
    }


    public LocationResult locationResult = new LocationResult() {

        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            try {
                final double Longitude = location.getLongitude();

                final double Latitude = location.getLatitude();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Log.e("lat", Longitude + "" + Latitude + "");
                   /* Toast.makeText(getApplicationContext(), "Got Location",
                            Toast.LENGTH_LONG).show();*/
                    }
                });


                SharedPreferences locationpref = getSharedPreferences(AppText.sharedPreferenceName, 0);
                SharedPreferences.Editor prefsEditor = locationpref.edit();
                prefsEditor.putString("emp_lang", Longitude + "");
                prefsEditor.putString("emp_lat", Latitude + "");
                prefsEditor.commit();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void setDateField() {
        DboEdt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                DboEdt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        //_signupButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

}
