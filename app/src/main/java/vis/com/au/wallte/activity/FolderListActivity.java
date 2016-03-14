package vis.com.au.wallte.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vis.com.au.Utility.AppConstant;
import vis.com.au.Utility.FolderItemPojo;
import vis.com.au.adapter.FolderItemAdapter;
import vis.com.au.helper.NetworkTask;
import vis.com.au.wallte.R;

/**
 * Created by Linchpin25 on 2/8/2016.
 */
public class FolderListActivity extends ActionBarActivity implements NetworkTask.Result {

    String folderId;
    ListView lvFolderElements;
    NetworkTask networkTask;
    private final int GET_DOC_LIST = 101;
    ArrayList<FolderItemPojo> folderItemPojoList;
    FolderItemAdapter adapter;
    private final int TAKE_PICTURE = 0;
    private final int SELECT_FILE = 1;
    private String resultUrl = "result.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_list_activity);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        getSupportActionBar().setTitle(getIntent().getStringExtra("folderName"));
        folderId = getIntent().getStringExtra("folderId");

        lvFolderElements = (ListView) findViewById(R.id.lvFolderElements);
        lvFolderElements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FolderItemPojo td = folderItemPojoList.get(position);
                Intent i = new Intent(FolderListActivity.this, DisplayUserDetails_Activity.class);
                i.putExtra("docId", td.getDocId());
                i.putExtra("docName", td.getFileTitle());
                startActivity(i);
            }
        });

        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "getFolderDoc"));
        listValue.add(new BasicNameValuePair("Type", "1"));
        listValue.add(new BasicNameValuePair("folderId", folderId));
        listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));

        networkTask = new NetworkTask(FolderListActivity.this, GET_DOC_LIST, listValue);
        networkTask.exposePostExecute(FolderListActivity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");


    }

    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if (id == GET_DOC_LIST) {
            if (object != null && !object.equals(""))
                fillData(object);
        }

    }

    private void fillData(String result) {
        try {
            JSONArray main = new JSONArray(result);
            FolderItemPojo folderItem;
            folderItemPojoList = new ArrayList<FolderItemPojo>();
            for (int i = 0; i < main.length(); i++) {
                folderItem = new FolderItemPojo();
                JSONObject child = main.getJSONObject(i);
                folderItem.setFileId(child.getString("file_id"));
                folderItem.setFilePath(child.getString("file_path"));
                folderItem.setFileType(child.getString("file_type"));
                folderItem.setUserId(child.getString("user_id"));
                folderItem.setDocId(child.getString("doc_id"));
                folderItem.setFileTitle(child.getString("file_title"));
                folderItem.setFileDesc(child.getString("file_desc"));
                folderItem.setDateOfIssue(child.getString("date_of_issue"));
                folderItem.setDateOfExpiry(child.getString("date_of_expire"));
                folderItem.setSupplierName(child.getString("supplier_name"));
                folderItem.setCreatedDate(child.getString("created_date"));
                folderItem.setSupplierName(child.getString("supplier_name"));

                folderItemPojoList.add(folderItem);

            }

            adapter = new FolderItemAdapter(FolderListActivity.this, folderItemPojoList);
            lvFolderElements.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.folder_item_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.takeSnap:
                captureImageFromCamera();
                break;
            case R.id.selectFromGallery:
                captureImageFromSdCard();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void captureImageFromSdCard() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, SELECT_FILE);
    }

    public void captureImageFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);
    }


    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ABBYY Cloud OCR SDK Demo App");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg");

        return mediaFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        String imageFilePath = null;

        switch (requestCode) {
            case TAKE_PICTURE:
                imageFilePath = getOutputMediaFileUri().getPath();
                break;
            case SELECT_FILE: {
                Uri imageUri = data.getData();

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cur = managedQuery(imageUri, projection, null, null, null);
                cur.moveToFirst();
                imageFilePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            break;
        }

        //Remove output file
        deleteFile(resultUrl);

        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra("IMAGE_PATH", imageFilePath);
        results.putExtra("RESULT_PATH", resultUrl);
        startActivity(results);
    }


}
