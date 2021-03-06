package vis.com.au.wallte.activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import vis.com.au.helper.NetworkTask;
import vis.com.au.ocr.AsyncProcessTask;
import vis.com.au.wallte.R;

public class ResultsActivity extends Activity implements NetworkTask.Result{

    String outputPath;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        tv = (TextView) findViewById(R.id.txtResult);
        String imageUrl = "unknown";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageUrl = extras.getString("IMAGE_PATH");
            outputPath = extras.getString("RESULT_PATH");
        }
        // Starting recognition process
        new AsyncProcessTask(this).execute(imageUrl, outputPath);
    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(outputPath);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    contents.append(text).append(System.getProperty("line.separator"));
                }
            } finally {
                fis.close();
            }

            displayMessage(contents.toString());
        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }

    public void displayMessage(String text) {
        //if(text!=null && text.equals(""))
            finish();
        tv.post(new MessagePoster(text));
        String name="TestDoc";
        String dateOfIssue="2011-11-11";
        String dateOfExpire="2017-11-11";
        String regostrationId="111";
        try {
            if (!text.equals("")) {
                if (text.contains("Name"))
                    name = text.substring(text.indexOf("Name")+10,text.indexOf("Name")+100).trim().replace("\n","");
                if (text.contains("Date of Issue"))
                    dateOfIssue = text.substring(text.indexOf("Date of Issue")+15,text.indexOf("Date of Issue")+100).trim().replace("\n", "").replace("/","-");
                /*if (text.contains("Date of Expiry"))
                    dateOfExpire = text.substring(name.indexOf("Date of Expiry"), name.indexOf("Date of Expiry") + 12);*/
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent intent = new Intent(ResultsActivity.this, EditDocumentScreen.class);
        intent.putExtra("docId", "");
        intent.putExtra("documentTypeTextView", "");
        intent.putExtra("documentTitleEditTextView",name);
        intent.putExtra("dateOfIssueTextView", dateOfIssue);
        intent.putExtra("dateOfExpireTextView",dateOfExpire);
        intent.putExtra("supliersNameTextView", "John Shens");
        intent.putExtra("registrationIdTextView","111");
        intent.putExtra("additionalInformationTextView","");
        intent.putExtra("comingFrom",true);
        startActivity(intent);
    }

  /*  private void sendFetchedTextToServer() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("type", "employee"));
        listValue.add(new BasicNameValuePair("dateOfIssue", "2011-11-11"));
        listValue.add(new BasicNameValuePair("dateOfExpire", "2017-11-11"));
        listValue.add(new BasicNameValuePair("documentType", "personal"));
        listValue.add(new BasicNameValuePair("registrationId", "111"));
        listValue.add(new BasicNameValuePair("addInfo", "hello"));
        listValue.add(new BasicNameValuePair("userType", "employee"));
        listValue.add(new BasicNameValuePair("suplierName", "employee"));
        listValue.add(new BasicNameValuePair("documentTitle", "Test Title"));
        listValue.add(new BasicNameValuePair("extraTrainingType", "hello"));
        listValue.add(new BasicNameValuePair("avatars", ""));
        listValue.add(new BasicNameValuePair("backAvatars",""));
        networkTask = new NetworkTask(ResultsActivity.this, 1, listValue);
        networkTask.exposePostExecute(ResultsActivity.this);
        networkTask.execute(AppConstant.uploadDocument);
    }*/

    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if(object!=null && !object.equals("")) {
            try {
                JSONObject jsonObject=new JSONObject(object);
                Toast.makeText(this,jsonObject.optString("Message"),Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                Toast.makeText(this,"Something went wrong from server",Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    class MessagePoster implements Runnable {
        public MessagePoster(String message) {
            _message = message;
        }

        public void run() {
            tv.append(_message + "\n");
        }

        private final String _message;
    }
}
