package vis.com.au.wallte.activity;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.squareup.picasso.Picasso;

import vis.com.au.Utility.AddDocumentType;
import vis.com.au.Utility.AppText;
import vis.com.au.adapter.DocumentSpinnerAdapter;
import vis.com.au.helper.NetworkTask;
import vis.com.au.support.Httprequest;
import vis.com.au.wallte.R;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditDocumentScreen extends ActionBarActivity implements NetworkTask.Result,OnClickListener{

    private EditText documentTitleEditTextView, additionalInformationTextView, supliersNameTextView, registrationIdTextView;
    private String filterTextName, filterTextOfIssue, filterTextOfExpire, filterTextOfRegId, documentType, documentTitle, addInfo;
    private TextView  dateOfIssueTextView, dateOfExpireTextView;
    private String sendImageToServer;
    private ImageView snapCardImageView;
    private Button snapImageViewButton, submitBtn, reScan;
    private static final int CAMERA_REQUEST = 1888;
    String Id;
    private Spinner documentTypesSpinner;
    private ArrayList<AddDocumentType> documentSpinnerItems;
    private DocumentSpinnerAdapter documentSpinnerAdapter;
    private NetworkTask networkTask;
    private boolean isComingFrom;
    private String[] docNames = {"FORKLIFT /ORDER PICKER", "SCAFFOLDING / DOGGING / RIGGING",
            "CRANES", "EARTHMOVING", "ELEVATED WORK PLATFORM", "TRADE CERTIFICATE",
            "CONSTRUCTION INDUCTION CARD", "SITE INDUCTION CARDS", "MISC", "OTHER"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_the_form);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_yello);
        getSupportActionBar().setTitle("Card Translation");
        UIEInitallization();
        filterTextName = getIntent().getStringExtra("supliersNameTextView");
        filterTextOfIssue = getIntent().getStringExtra("dateOfIssueTextView");
        filterTextOfExpire = getIntent().getStringExtra("dateOfExpireTextView");
        filterTextOfRegId = getIntent().getStringExtra("registrationIdTextView");
        String docTitle = getIntent().getStringExtra("documentTitleEditTextView");
        documentTitleEditTextView.setText(docTitle);
        isComingFrom=getIntent().getBooleanExtra("comingFrom",false);
        supliersNameTextView.setText(filterTextName);
        dateOfIssueTextView.setText(filterTextOfIssue);//filterTextOfIssue
        dateOfExpireTextView.setText(filterTextOfExpire);
        registrationIdTextView.setText(filterTextOfRegId);
        setTheDocumentType();
        Id = getIntent().getStringExtra("docId");
        SharedPreferences sharedPref = getSharedPreferences(AppText.sharedPreferenceName, 0);
        Editor edit = sharedPref.edit();
        edit.putString("idForUpDate", Id);
        edit.commit();
        Log.e("DocumentId at :", Id + "");
        if (Id.equals("")) {
            submitBtn.setVisibility(View.GONE);
        } else {
            needToUpDate();
            submitBtn.setVisibility(View.VISIBLE);
            reScan.setVisibility(View.GONE);
            submitBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAllTheFieldToServer();
                }
            });
        }

        //snapImageButton Action
        snapImageViewButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        if(isComingFrom) {
            submitBtn.setText("SAVE");
        }
    }

    private void UIEInitallization() {
        //documentTypeTextView 		  = (EditText) findViewById(R.id.documentTypeTextView);
        documentTypesSpinner = (Spinner) findViewById(R.id.documentTypesSpinner);
        dateOfIssueTextView = (TextView) findViewById(R.id.dateOfIssueTextView);
        dateOfExpireTextView = (TextView) findViewById(R.id.dateOfExpireTextView);
        supliersNameTextView = (EditText) findViewById(R.id.supliersNameTextView);
        registrationIdTextView = (EditText) findViewById(R.id.registrationIdTextView);
        documentTitleEditTextView = (EditText) findViewById(R.id.documentTitleEditTextView);
        additionalInformationTextView = (EditText) findViewById(R.id.additionalInformationTextView);
        snapCardImageView = (ImageView) findViewById(R.id.attachmentImageView);
        snapImageViewButton = (Button) findViewById(R.id.snapImageViewButton);
        dateOfExpireTextView.setOnClickListener(this);
        dateOfIssueTextView.setOnClickListener(this);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        reScan = (Button) findViewById(R.id.reScan);

        reScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(EditDocumentScreen.this,"Click on menu to take a Image",Toast.LENGTH_SHORT).show();
                finish();
                //Intent intent = new Intent(EditDocumentScreen.this,CaptureActivity.class);
                //startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            snapCardImageView.setImageBitmap(photo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            sendImageToServer = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            SharedPreferences pref = getSharedPreferences(AppText.sharedPreferenceName, 0);
            Editor edit = pref.edit();
            edit.putString("newImageAdd", sendImageToServer);
            edit.commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.fill_the_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionSaveBtn:
                if(isComingFrom)
                    sendFetchedTextToServer();
                else
                setAllTheFieldToServer();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAllTheFieldToServer() {
        final ProgressDialog pg = AppText.progressDialog(EditDocumentScreen.this, "Connecting with server...");
        pg.show();
        try {

            final JSONObject jObj = new JSONObject();
            jObj.put("postType", AppText.postTypeUser);
            jObj.put("emp_id", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("empId", null));
            jObj.put("suplierName", supliersNameTextView.getText().toString());

            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat fmtFSer = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            if (getSharedPreferences(AppText.sharedPreferenceName, 0).getString("idForUpDate", null) != null) {
                try {
                    Date date = fmtFSer.parse(dateOfIssueTextView.getText().toString());
                    String dateIssue = fmtOut.format(date);
                    jObj.put("dateOfIssue", dateIssue);
                    Log.e("dateIssue at not -1", dateIssue + "");

                    Date dateE = fmtFSer.parse(dateOfExpireTextView.getText().toString());
                    String dateExp = fmtOut.format(dateE);
                    jObj.put("dateOfExpire", dateExp);
                    Log.e("dateIssue at not -1", dateExp + "");
                } catch (ParseException e) {
                    //dateOfIssueTextView.setError("the date you provided is in an invalid date,clear character like '.' and space");
                    e.printStackTrace();
                }

            } else {
                try {
                    Date date = fmt.parse(dateOfIssueTextView.getText().toString());//dateOfIssueTextView.getText().toString()
                    String dateIssue = fmtOut.format(date);
                    jObj.put("dateOfIssue", dateIssue);
                    Log.e("dateIssue at ", dateIssue + "");

                    Date dateE = fmt.parse(dateOfExpireTextView.getText().toString());
                    String dateExp = fmtOut.format(dateE);
                    jObj.put("dateOfExpire", dateExp);
                } catch (ParseException e) {
                    final String date = filterTextOfIssue;
                    if (!isValidDate(date)) {
                        dateOfIssueTextView.setError("the date you provided is in an invalid date,clear character like '.' and space");
                    }
                    e.printStackTrace();
                    //finish();
                }

            }
            jObj.put("registrationId", registrationIdTextView.getText().toString());
            jObj.put("documentType", documentSpinnerItems.get(documentTypesSpinner.getSelectedItemPosition()).getDocNames());
            jObj.put("documentTitle", documentTitleEditTextView.getText().toString());
            jObj.put("addInfo", additionalInformationTextView.getText().toString());
            if (sendImageToServer == null) {
                jObj.put("avatar", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("newImageAdd", null));
                Log.e("hello", sendImageToServer + "");
            } else {
                jObj.put("avatar", sendImageToServer);
                Log.e("helloImage", sendImageToServer + "");
            }
            Log.e("file send", jObj + "");
            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (getSharedPreferences(AppText.sharedPreferenceName, 0).getString("idForUpDate", null) != null) {
                        try {
                            jObj.put("docId", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("uIdServer", null));
                            jObj.put("fileId", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("fIdServer", null));
                            Httprequest.makeHttpRequest(jObj.toString(), AppText.editDocument);

                        } catch (JSONException e) {
                            Log.e("exception comes", e.toString());
                            e.printStackTrace();
                        }
                        Log.e("editDocumetnSend::", jObj + "");
                    } else {
                        Httprequest.makeHttpRequest(jObj.toString(), AppText.upLoadData);
                    }
                    String returnValuePassToAnotherActivity = Httprequest.retValue;
                    Log.e("returnValuePass", returnValuePassToAnotherActivity + "");
                    try {
                        JSONObject jObject = new JSONObject(returnValuePassToAnotherActivity);
                        String userIdFromServer = jObject.getString("doc_Id");
                        String fileIdFromServer = jObject.getString("fileId");
                        SharedPreferences sPre = getSharedPreferences(AppText.sharedPreferenceName, 0);
                        Editor edit = sPre.edit();
                        edit.putString("uIdServer", userIdFromServer);
                        edit.putString("fIdServer", fileIdFromServer);
                        edit.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                   /* pg.dismiss();
                    Intent intent = new Intent(EditDocumentScreen.this, DashboardActivity.class);
                    intent.putExtra("retValue", returnValuePassToAnotherActivity);
                    intent.putExtra("editId", Id + "");
                    startActivity(intent);*/

                }
            }).start();

        } catch (Exception e) {
            pg.dismiss();
            e.printStackTrace();
        }
    }

    private void needToUpDate() {

        //documentTypeTextView 		  = (EditText) findViewById(R.id.documentTypeTextView);
        documentTypesSpinner = (Spinner) findViewById(R.id.documentTypesSpinner);
        dateOfIssueTextView = (TextView) findViewById(R.id.dateOfIssueTextView);
        dateOfExpireTextView = (TextView) findViewById(R.id.dateOfExpireTextView);
        supliersNameTextView = (EditText) findViewById(R.id.supliersNameTextView);
        registrationIdTextView = (EditText) findViewById(R.id.registrationIdTextView);
        documentTitleEditTextView = (EditText) findViewById(R.id.documentTitleEditTextView);
        additionalInformationTextView = (EditText) findViewById(R.id.additionalInformationTextView);
        dateOfIssueTextView.setOnClickListener(EditDocumentScreen.this);
        dateOfExpireTextView.setOnClickListener(EditDocumentScreen.this);
        //documentTypeTextView.setText(getIntent().getStringExtra("documentTypeTextView"));
        String dateI = getIntent().getStringExtra("dateOfIssueTextView");
        String dateE = getIntent().getStringExtra("dateOfExpireTextView");
        String sName = getIntent().getStringExtra("supliersNameTextView");
        String aInfo = getIntent().getStringExtra("additionalInformationTextView");
        String regId = getIntent().getStringExtra("registrationIdTextView");
        String docTitle = getIntent().getStringExtra("documentTitleEditTextView");

        //dateI = dateOfIssueTextView.getText().toString();
        if (dateI.length() > 0) {
            dateI = dateI.substring(0, 10);
        }

        //dateE = dateOfExpireTextView.getText().toString();
        if (dateE.length() > 0) {
            dateE = dateE.substring(0, 10);
        }

        SharedPreferences pref = getSharedPreferences(AppText.sharedPreferenceName, 0);
        Editor edit = pref.edit();
        edit.putString("dateI", dateI);
        edit.putString("dateE", dateE);
        edit.putString("sName", sName);
        edit.putString("aInfo", aInfo);
        edit.putString("regId", regId);
        edit.putString("docTitle", docTitle);
        edit.commit();


        documentSpinnerItems.get(documentTypesSpinner.getSelectedItemPosition()).getDocNames();
        dateOfIssueTextView.setText(dateI);
        dateOfExpireTextView.setText(dateE);
        supliersNameTextView.setText(sName);
        additionalInformationTextView.setText(aInfo);
        registrationIdTextView.setText(regId);
        documentTitleEditTextView.setText(docTitle);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener() {

            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        builder.build().load(getSharedPreferences(AppText.sharedPreferenceName, 0).getString("certImage", null)).placeholder(R.drawable.suscribe).error(R.drawable.sss_error).into(snapCardImageView);
    }

    private void setTheDocumentType() {
        documentSpinnerItems = new ArrayList<AddDocumentType>();
        for (int k = 0; k < docNames.length; k++) {
            AddDocumentType addDocumentName = new AddDocumentType();
            addDocumentName.setDocNames(docNames[k]);
            documentSpinnerItems.add(addDocumentName);
        }
        documentSpinnerAdapter = new DocumentSpinnerAdapter(documentSpinnerItems);
        documentTypesSpinner.setAdapter(documentSpinnerAdapter);

    }

    private boolean isValidDate(String date) {
        if (date != null && date.length() == 10) {
            return true;
        }
        return false;
    }

    private void sendFetchedTextToServer() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppText.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("type", "employee"));
        listValue.add(new BasicNameValuePair("dateOfIssue", dateOfIssueTextView.getText().toString()));
        listValue.add(new BasicNameValuePair("dateOfExpire", dateOfExpireTextView.getText().toString()));
        listValue.add(new BasicNameValuePair("documentType", "personal"));
        listValue.add(new BasicNameValuePair("registrationId", registrationIdTextView.getText().toString()));
        listValue.add(new BasicNameValuePair("addInfo", additionalInformationTextView.getText().toString()));
        listValue.add(new BasicNameValuePair("userType", "employee"));
        listValue.add(new BasicNameValuePair("suplierName", supliersNameTextView.getText().toString()));
        listValue.add(new BasicNameValuePair("documentTitle",documentTitleEditTextView.getText().toString()));
        listValue.add(new BasicNameValuePair("extraTrainingType", "hello"));
        listValue.add(new BasicNameValuePair("avatars", ""));
        listValue.add(new BasicNameValuePair("backAvatars",""));
        networkTask = new NetworkTask(EditDocumentScreen.this, 1, listValue);
        networkTask.exposePostExecute(EditDocumentScreen.this);
        networkTask.execute(AppText.uploadDocument);
    }
    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if(object!=null && !object.equals("")) {
            try {
                JSONObject jsonObject=new JSONObject(object);
                Toast.makeText(this, jsonObject.optString("Message"), Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                Toast.makeText(this,"Something went wrong from server",Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
    void showCalendar(final TextView textView)
    {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                textView.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.dateOfIssueTextView:
                showCalendar(dateOfIssueTextView);
                break;
            case R.id.dateOfExpireTextView:
                showCalendar(dateOfExpireTextView);
                break;
        }
    }
}
