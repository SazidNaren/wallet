package vis.com.au.wallte.activity;

import com.squareup.picasso.Picasso;

import vis.com.au.helper.NetworkTask;
import vis.com.au.Utility.AppConstant;
import vis.com.au.Utility.DocumentDetailBean;
import vis.com.au.wallte.R;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayUserDetails_Activity extends ActionBarActivity implements NetworkTask.Result {

    private TextView documentTypeTextView, documentTitleEditTextView, dateOfIssueTextView, dateOfExpireTextView,
            supliersNameTextView, registrationIdTextView, additionalInformationTextView;
    private ImageView attachmentImageView;
    private Button editButton;
    public static String Id;
    NetworkTask networkTask;
    ArrayList<DocumentDetailBean> docList;
    DocumentDetailBean docDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayuserdetails);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        getSupportActionBar().setTitle(getIntent().getStringExtra("docName"));
        //return output:
        // {"MessageID":"34532","Message":"File has been successfully uploaded.","userType":"","userId":"7","fileId":9,"documentTitle":"licen","documentType":"aaa","addInfo":"hello m here","dateOfIssue":" itself gives you","dateOfExpire":" ability to revie","suplierName":" Note that the ca","registrationId":" 123456","UploadedDate":"2015-09-09 07:47:34"}
        Id = getIntent().getStringExtra("docId");
        Log.e("docId at DisplayUser", Id + "");
        UIEInitiallization();
        setContentToRespectiveFields();

    }

    private void UIEInitiallization() {
        documentTypeTextView = (TextView) findViewById(R.id.documentTypeTextView);
        documentTitleEditTextView = (TextView) findViewById(R.id.documentTitleEditTextView);
        dateOfIssueTextView = (TextView) findViewById(R.id.dateOfIssueTextView);
        dateOfExpireTextView = (TextView) findViewById(R.id.dateOfExpireTextView);
        supliersNameTextView = (TextView) findViewById(R.id.supliersNameTextView);
        registrationIdTextView = (TextView) findViewById(R.id.registrationIdTextView);
        additionalInformationTextView = (TextView) findViewById(R.id.additionalInformationTextView);
        attachmentImageView = (ImageView) findViewById(R.id.attachmentImageView);
        editButton = (Button) findViewById(R.id.editeButton);

        editButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayUserDetails_Activity.this, EditDocumentScreen.class);
                intent.putExtra("docId", Id);
                intent.putExtra("documentTypeTextView", documentTypeTextView.getText());
                intent.putExtra("documentTitleEditTextView", documentTitleEditTextView.getText());
                intent.putExtra("dateOfIssueTextView", dateOfIssueTextView.getText());
                intent.putExtra("dateOfExpireTextView", dateOfExpireTextView.getText());
                intent.putExtra("supliersNameTextView", supliersNameTextView.getText());
                intent.putExtra("registrationIdTextView", registrationIdTextView.getText());
                intent.putExtra("additionalInformationTextView", additionalInformationTextView.getText());
                intent.putExtra("comingFrom",false);
                startActivity(intent);
                finish();

            }
        });
    }

    private void setContentToRespectiveFields() {
        /*Cursor cursor = DatabaseHelper.readQuery(getApplicationContext(), "SELECT documentTitle,documentType,addInfo,dateOfIssue,dateOfExpire,suplierName,registratinId,empImage FROM tblDocuments WHERE docId ='"+Id+"'");//getIntent().getIntExtra("docId",-1)
		if(cursor!=null){
			if(cursor.moveToFirst()){
				do {
					documentTypeTextView.setText(cursor.getString(1));
					documentTitleEditTextView.setText(cursor.getString(0));
					dateOfIssueTextView.setText(cursor.getString(3).substring(0,10));
					dateOfExpireTextView.setText(cursor.getString(4).substring(0,10));
					supliersNameTextView.setText(cursor.getString(5));
					registrationIdTextView.setText(cursor.getString(6));
					additionalInformationTextView.setText(cursor.getString(2));
					Picasso.Builder builder = new Picasso.Builder(this);
					builder.listener(new Picasso.Listener() {
						
						@Override
						public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
							exception.printStackTrace();
						}
					});
					builder.build().load(cursor.getString(7)).placeholder(R.drawable.toggle_btn).error(R.drawable.sss_error).into(attachmentImageView);
					
				} while (cursor.moveToNext());
			}
		}*/

        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("emp_id", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("type", "employee"));
        listValue.add(new BasicNameValuePair("data", "ALL"));
        networkTask = new NetworkTask(DisplayUserDetails_Activity.this, 1, listValue);
        networkTask.exposePostExecute(DisplayUserDetails_Activity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/document_of_user.php");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_user_details_, menu);
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
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if (id == 1) {
            //  Toast.makeText(DashboardActivity.this, object, Toast.LENGTH_LONG).show();
            getDocuments(object);
        }

    }


    private void getDocuments(String jsonGot) {
        try {
            docList = new ArrayList<DocumentDetailBean>();
            JSONArray jsonArray = new JSONArray(jsonGot);
            for (int i = 0; i < jsonArray.length(); i++) {
                docDetail = new DocumentDetailBean();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("doc_id") != null && !jsonObject.getString("doc_id").equals("") && jsonObject.getString("doc_id").equals(Id)) {
                    docDetail.setFrontImage(jsonObject.getString("0"));
                    docDetail.setBackImage(jsonObject.getString("1"));
                    docDetail.setFileId(jsonObject.getString("file_id"));
                    docDetail.setFilePath(jsonObject.getString("file_path"));
                    docDetail.setFileType(jsonObject.getString("file_type"));
                    docDetail.setDocId(jsonObject.getString("doc_id"));
                    docDetail.setRefTable(jsonObject.getString("ref_table"));
                    docDetail.setUserId(jsonObject.getString("user_id"));
                    docDetail.setFileTitle(jsonObject.getString("file_title"));
                    docDetail.setFileDesc(jsonObject.getString("file_desc"));
                    docDetail.setFolderId(jsonObject.getString("folder_id"));
                    docDetail.setDateOfIssue(jsonObject.getString("date_of_issue"));
                    docDetail.setDateOfExpire(jsonObject.getString("date_of_expire"));
                    docDetail.setSupplierName(jsonObject.getString("supplier_name"));
                    docDetail.setBackId(jsonObject.getString("back_id"));
                    docDetail.setExtraTrainingType(jsonObject.getString("extraTrainingType"));
                    docDetail.setCol3(jsonObject.getString("col3"));
                    docDetail.setCol4(jsonObject.getString("col4"));
                    docDetail.setRegId(jsonObject.getString("reg_id"));
                    docDetail.setCreatedDate(jsonObject.getString("created_date"));
                    docDetail.setStatus(jsonObject.getString("status"));

                    setDataIntoFields(docDetail);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDataIntoFields(DocumentDetailBean documentDetailBean) {
        documentTypeTextView.setText(documentDetailBean.getFileType());
        documentTitleEditTextView.setText(documentDetailBean.getFileTitle());
        dateOfIssueTextView.setText(documentDetailBean.getDateOfIssue());
        dateOfExpireTextView.setText(documentDetailBean.getDateOfExpire());
        supliersNameTextView.setText(documentDetailBean.getSupplierName());
        registrationIdTextView.setText(documentDetailBean.getRegId());
        additionalInformationTextView.setText(documentDetailBean.getExtraTrainingType());
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener() {

            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        builder.build().load(documentDetailBean.getFrontImage()).placeholder(R.drawable.toggle_btn).error(R.drawable.sss_error).into(attachmentImageView);

    }


}
