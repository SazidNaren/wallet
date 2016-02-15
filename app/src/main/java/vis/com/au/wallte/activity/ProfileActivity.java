package vis.com.au.wallte.activity;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import vis.com.au.Utility.AppText;
import vis.com.au.support.Httprequest;
import vis.com.au.wallte.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements OnClickListener {
    private EditText userFirstName, userLastName, userNameEditText, userDateOfBirthEditText,
            userPhoneNo, userAddress, userPassword, userEmail, userName;
    private TextView emailTV, empLatLngTextView;
    private ImageView profile_image, iv_back;
    private String fileName;
    private Button editProfileBtn;
    private boolean profileEdit = true;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        getSupportActionBar().setTitle("Edit Profile");

        emailTV = (TextView) findViewById(R.id.emailTV);
        userFirstName = (EditText) findViewById(R.id.userFirstName);
        userLastName = (EditText) findViewById(R.id.userLastName);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        userDateOfBirthEditText = (EditText) findViewById(R.id.userDateOfBirthEditText);
        userPhoneNo = (EditText) findViewById(R.id.userPhoneNo);
        userEmail = (EditText) findViewById(R.id.userEmail);

        userAddress = (EditText) findViewById(R.id.userAddress);
        userName = (EditText) findViewById(R.id.userNameEditText);
        empLatLngTextView = (TextView) findViewById(R.id.empLatLng);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        setTheValuesInField();
        fieldsDisable();
        editProfileBtn = (Button) findViewById(R.id.editProfileBtn);
        editProfileBtn.setText("EDIT");
        editProfileBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profileEdit) {
                    profileEdit = false;
                    fieldsEnable();
                    editProfileBtn.setText("SAVE");

                } else {

                    final ProgressDialog pg = AppText.progressDialog(ProfileActivity.this, "Progressing for update...");
                    pg.show();
                    SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
                    final String empId = shared.getString("empId", null);
                    if (empId == null) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Intent inte = new Intent(ProfileActivity.this, LogInActivity.class);
                                inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(inte);
                            }
                        });
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final JSONObject jobj = new JSONObject();
                                    jobj.put("postType", AppText.postTypeUser);
                                    jobj.put("emp_id", empId);
                                    jobj.put("emp_email", emailTV.getText().toString());
                                    jobj.put("emp_first_name", userFirstName.getText().toString());
                                    jobj.put("emp_last_name", userLastName.getText().toString());
                                    jobj.put("emp_DOB", userDateOfBirthEditText.getText().toString());
                                    jobj.put("emp_contact_no", userPhoneNo.getText().toString());
                                    jobj.put("emp_address", userAddress.getText().toString());
                                    jobj.put("emp_email", userEmail.getText().toString());
                                    jobj.put("emp_username", userName.getText().toString());
                                    if (fileName == null) {
                                        jobj.put("emp_avatar", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("oldImage", null));
                                    } else {
                                        jobj.put("emp_avatar", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("userImage", null));
                                    }
                                    jobj.put("emp_lat", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("Latitude", ""));
                                    jobj.put("emp_lang", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("Longitude", ""));

                                    Httprequest.makeHttpRequest(jobj.toString(), AppText.empProfile);
                                    Log.e("data send to server", jobj + "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        String returnFromEmpProfile = Httprequest.retValue;
                                        Log.e("returnValue", returnFromEmpProfile + "");
                                        try {
                                            JSONObject jObject = new JSONObject(returnFromEmpProfile);
                                            String empId = jObject.getString("emp_id");
                                            SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
                                            Editor ed = shared.edit();
                                            ed.putString("empId", empId);
                                            ed.putString("empInfo", jObject.toString());
                                            ed.commit();
                                            AppText.showToast(ProfileActivity.this, "sucessfully updated");
                                            //restart Activity
                                            //startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                            pg.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Your profile is updated successfully", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }).start();
                    }


                    profileEdit = true;
                    fieldsDisable();
                    editProfileBtn.setText("EDIT");


                }
            }
        });


        /*final AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertDialog.setTitle("Do you want to edit your profile.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                setTheValuesInField();

                editProfileBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog pg = AppText.progressDialog(ProfileActivity.this, "Progressing for update...");
                        pg.show();
                        SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
                        final String empId = shared.getString("empId", null);
                        if (empId == null) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Intent inte = new Intent(ProfileActivity.this, LogInActivity.class);
                                    inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(inte);
                                }
                            });
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final JSONObject jobj = new JSONObject();
                                        jobj.put("postType", AppText.postTypeUser);
                                        jobj.put("emp_id", empId);
                                        jobj.put("emp_email", emailTV.getText().toString());
                                        jobj.put("emp_first_name", userFirstName.getText().toString());
                                        jobj.put("emp_last_name", userLastName.getText().toString());
                                        jobj.put("emp_DOB", userDateOfBirthEditText.getText().toString());
                                        jobj.put("emp_contact_no", userPhoneNo.getText().toString());
                                        jobj.put("emp_address", userAddress.getText().toString());
                                        jobj.put("emp_username", userName.getText().toString());
                                        if (fileName == null) {
                                            jobj.put("emp_avatar", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("oldImage", null));
                                        } else {
                                            jobj.put("emp_avatar", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("userImage", null));
                                        }
                                        jobj.put("emp_lat", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("Latitude", ""));
                                        jobj.put("emp_lang", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("Longitude", ""));

                                        Httprequest.makeHttpRequest(jobj.toString(), AppText.empProfile);
                                        Log.e("data send to server", jobj + "");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            String returnFromEmpProfile = Httprequest.retValue;
                                            Log.e("returnValue", returnFromEmpProfile + "");
                                            try {
                                                JSONObject jObject = new JSONObject(returnFromEmpProfile);
                                                String empId = jObject.getString("emp_id");
                                                SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
                                                Editor ed = shared.edit();
                                                ed.putString("empId", empId);
                                                ed.putString("empInfo", jObject.toString());
                                                ed.commit();
                                                AppText.showToast(ProfileActivity.this, "sucessfully updated");
                                                //restart Activity
                                                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                                pg.dismiss();
                                                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                                                finish();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });


            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                editProfileBtn.setVisibility(View.GONE);
                setTheValuesInField();
            }
        });

        alertDialog.show();*/
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

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTheValuesInField() {
        try {
            SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
            JSONObject jObj = new JSONObject(shared.getString("empInfo", null));
            Log.e("returnFirst", jObj + "");
            emailTV.setText(jObj.getString("emp_email"));
            userFirstName.setText(jObj.getString("emp_first_name"));
            userLastName.setText(jObj.getString("emp_last_name"));
            userDateOfBirthEditText.setText(jObj.getString("emp_DOB"));
            userPhoneNo.setText(jObj.getString("emp_contact_no"));
            userAddress.setText(jObj.getString("emp_address"));
            userEmail.setText(jObj.getString("emp_email"));
            String lat = jObj.getString("emp_lat");
            String lng = jObj.getString("emp_lang");
            /*SharedPreferences sharedPre = getSharedPreferences(AppText.sharedPreferenceName, 0);
            Editor edit = sharedPre.edit();
			edit.putString("empLat", lat);
			edit.putString("empLang", lng);
			edit.commit();*/
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            if (lat != null && lng != null && !lat.equals("") && !lng.equals("")) {
                List<Address> address = gcd.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                if (address.size() > 0) {
                    empLatLngTextView.setText(address.get(0).getLocality());
                }
            }
            String empImage = jObj.getString("emp_avatar");

            Log.e("empImage", empImage + "");
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            });
            builder.build().load(empImage).placeholder(R.drawable.suscribe).error(R.drawable.sss_error).into(profile_image);

            userNameEditText.setText(jObj.getString("emp_username"));


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
                profile_image.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                // Get the Image's file name
               /* String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];*/
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
                SharedPreferences shared = getSharedPreferences(AppText.sharedPreferenceName, 0);
                Editor edit = shared.edit();
                edit.putString("userImage", fileName);
                edit.commit();
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
            case R.id.editProfileBtn:
                fieldsEnable();
                editProfileBtn.setText("SAVE");
                break;

            default:
                break;
        }

    }

    public void fieldsDisable() {
        userFirstName.setEnabled(false);
        userLastName.setEnabled(false);
        userName.setEnabled(false);
        userDateOfBirthEditText.setEnabled(false);
        userPhoneNo.setEnabled(false);
        userAddress.setEnabled(false);
        profile_image.setEnabled(false);
        userEmail.setEnabled(false);

    }

    public void fieldsEnable() {
        userFirstName.setEnabled(true);
        userLastName.setEnabled(true);
        userName.setEnabled(true);
        userDateOfBirthEditText.setEnabled(true);
        userPhoneNo.setEnabled(true);
        userAddress.setEnabled(true);
        profile_image.setEnabled(true);
        userEmail.setEnabled(true);
    }


}
