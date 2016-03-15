package vis.com.au.wallte.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vis.com.au.Utility.AppConstant;
import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.helper.DocumentsComparator;
import vis.com.au.helper.NetworkTask;
import vis.com.au.Utility.TodayListView;
import vis.com.au.adapter.TodayListAdapter;
import vis.com.au.wallte.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends DrawerLayoutActivity implements NetworkTask.Result {

    private ListView todayUpLoadedListView;
    private TodayListAdapter todaysListAdapter;
    private List<TodayListView> todayList = new ArrayList<TodayListView>();
    private String recivReturnValue;
    String reciveEditId;
    private final int TAKE_PICTURE = 0;
    private final int SELECT_FILE = 1;
    private String resultUrl = "result.txt";
    private EditText searchUploadFileEditText;
    private NetworkTask networkTask;
    private Date todaysDate;
    private AppPreferences appPreferences;
    private final int GET_DOC_LIST = 101, CREATE_FOLDER = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_myupload);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_myupload, null, false);
        appPreferences=AppPreferences.getInstance(this);
        todaysDate = new Date();
        mDrawerLayout.addView(contentView, 0);
        todayUpLoadedListView = (ListView) findViewById(R.id.todayUpLoadedListView);

       /* List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("emp_id", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("type", "employee"));

        networkTask = new NetworkTask(DashboardActivity.this, GET_DOC_LIST, listValue);
        networkTask.exposePostExecute(DashboardActivity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/document_of_user.php");*/
        todayUpLoadedListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TodayListView td = todayList.get(position);
                if (!td.isFolder()) {
                    Intent i = new Intent(DashboardActivity.this, DisplayUserDetails_Activity.class);
                    i.putExtra("docId", td.docId);
                    i.putExtra("docName", td.getFileName());
                    startActivity(i);
                } else {
                    Intent i = new Intent(DashboardActivity.this, FolderListActivity.class);
                    i.putExtra("folderId", td.docId);
                    i.putExtra("folderName", td.getFileName());
                    startActivity(i);
                }

            }
        });

        todayUpLoadedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                showDialogForDeleteRename(position,todayList.get(position).isFolder());
                return true;
            }
        });

        searchUploadFileEditText = (EditText) findViewById(R.id.searchUploadFileEditText);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        searchUploadFileEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textLength = s.length();
                ArrayList<TodayListView> tempArrayList = new ArrayList<TodayListView>();
                for (TodayListView tlv : todayList) {
                    if (textLength <= tlv.getFileName().length()) {
                        if (tlv.getFileName().toLowerCase().contains(s.toString().toLowerCase())) {
                            tempArrayList.add(tlv);
                        }
                    }
                }
                todaysListAdapter = new TodayListAdapter(tempArrayList, DashboardActivity.this);
                todayUpLoadedListView.setAdapter(todaysListAdapter);
                appPreferences.setCountDoc(tempArrayList.size() + "");
                refreshDrawer();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void showDialogForDeleteRename(final int position,boolean isFolder) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        if(isFolder) {
            builder.setItems(R.array.manage_folder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                        createFolderDialog(position);
                    else
                        deleteFolder(position);
                    //rename
                }
            });
        }
        else
        {
            builder.setItems(R.array.manage_document, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                    {
                        Intent i = new Intent(DashboardActivity.this, DisplayUserDetails_Activity.class);
                        i.putExtra("docId", todayList.get(position).docId);
                        i.putExtra("docName", todayList.get(position).getFileName());
                        startActivity(i);
                    }
                    else
                        deleteDocument(position);
                    //rename
                }
            });
        }
        builder.show();
    }

    private void createFolderDialog(final int position) {

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_folder_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText etFolderName = (EditText) dialog.findViewById(R.id.etFolderName);
        etFolderName.setText(todayList.get(position).getFileName());
        TextView header=(TextView)dialog.findViewById(R.id.header);
        header.setText("Rename Folder");
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvCreate);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etFolderName.getText().toString().equals("")) {
                    dialog.dismiss();
                    renameFolder(etFolderName.getText().toString(),position);
                } else
                    Toast.makeText(DashboardActivity.this, "Folder name can't be empty", Toast.LENGTH_LONG).show();

            }
        });
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void deleteFolder(int position) {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "DeleteFolder"));
        listValue.add(new BasicNameValuePair("folderId", Integer.toString(Integer.parseInt(todayList.get(position).getDocId()))));
        networkTask = new NetworkTask(DashboardActivity.this, 1003, listValue);
        networkTask.exposePostExecute(DashboardActivity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
    }

    private void deleteDocument(int position) {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "DeleteDoc"));
        listValue.add(new BasicNameValuePair("docId", Integer.toString(Integer.parseInt(todayList.get(position).getDocId()))));
        networkTask = new NetworkTask(DashboardActivity.this, 1003, listValue);
        networkTask.exposePostExecute(DashboardActivity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
    }

    private void renameFolder(String s, int position) {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "RenameFolder"));
        listValue.add(new BasicNameValuePair("folderName", s));
        listValue.add(new BasicNameValuePair("folderId", Integer.toString(Integer.parseInt(todayList.get(position).getDocId()))));
        networkTask = new NetworkTask(DashboardActivity.this, 1004, listValue);
        networkTask.exposePostExecute(DashboardActivity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
    }

    private void hitAllDataApi() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "getAllDocFolder"));
        listValue.add(new BasicNameValuePair("Type", "1"));
        listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("fetch", "All"));

        networkTask = new NetworkTask(DashboardActivity.this, GET_DOC_LIST, listValue);
        networkTask.exposePostExecute(DashboardActivity.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(appPreferences.isPaidVersion())
        getMenuInflater().inflate(R.menu.paid_menu, menu);
        else
            getMenuInflater().inflate(R.menu.unpaid_menu, menu);

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
            case R.id.createFolder:
                createFolderDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createFolderDialog() {

        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_folder_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText etFolderName = (EditText) dialog.findViewById(R.id.etFolderName);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvCreate);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etFolderName.getText().toString().equals("")) {
                    dialog.dismiss();
                    List<NameValuePair> listValue = new ArrayList<NameValuePair>();
                    listValue.add(new BasicNameValuePair("userId",Integer.toString(Integer.parseInt(getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")))));
                    listValue.add(new BasicNameValuePair("folderName", etFolderName.getText().toString()));
                    listValue.add(new BasicNameValuePair("type", Integer.toString(1)));
                    listValue.add(new BasicNameValuePair("actions", "NewFolder"));
                    networkTask = new NetworkTask(DashboardActivity.this, CREATE_FOLDER, listValue);
                    networkTask.exposePostExecute(DashboardActivity.this);
                    networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");

                } else
                    Toast.makeText(DashboardActivity.this, "Folder name can't be empty", Toast.LENGTH_LONG).show();

            }
        });
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();


    }


    private void getDocuments(String jsonGot) {
        try {
            JSONArray jsonArray = new JSONArray(jsonGot);
            for (int i = 0; i < jsonArray.length(); i++) {
                TodayListView todayListView;
                JSONArray jsonSubArray = jsonArray.getJSONArray(i);
                for (int j = 0; j < jsonSubArray.length(); j++) {
                    todayListView = new TodayListView();
                    if (i == 0) {
                        JSONObject child = jsonSubArray.getJSONObject(j);
                        todayListView.setDocId(child.getString("folderId"));
                        todayListView.setFileName(child.getString("folderName"));
                        todayListView.setIsFolder(true);
                        todayListView.setCount(child.getString("Count"));

                        if (child.getString("folderCreated") != null && !child.getString("folderCreated").equals("")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date startDate;
                            try {
                                startDate = df.parse(child.getString("folderCreated"));
                                todayListView.setUpLoadedTime(startDate);
                                long diff = (todaysDate.getTime() - startDate.getTime()) / (60 * 60 * 1000);
                                long days = diff / 24;
                                if (days == 0) {
                                    todayListView.setUploadedDate("TODAY");
                                } else if (days == 1) {
                                    todayListView.setUploadedDate("YESTERDAY");
                                } else
                                    todayListView.setUploadedDate("BEFORE YESTERDAY");


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    } else if (i == 1) {
                        JSONObject child = jsonSubArray.getJSONObject(j);
                        todayListView.setDocId(child.getString("doc_id"));
                        todayListView.setFileName(child.getString("file_title"));
                        todayListView.setFilePath(child.getString("file_path"));
                        todayListView.setIsFolder(false);
                        if (child.getString("created_date") != null && !child.getString("created_date").equals("")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date startDate;
                            try {
                                startDate = df.parse(child.getString("created_date"));
                                todayListView.setUpLoadedTime(startDate);
                                long diff = (todaysDate.getTime() - startDate.getTime()) / (60 * 60 * 1000);
                                long days = diff / 24;
                                if (days == 0) {
                                    todayListView.setUploadedDate("TODAY");
                                } else if (days == 1) {
                                    todayListView.setUploadedDate("YESTERDAY");
                                } else
                                    todayListView.setUploadedDate("BEFORE YESTERDAY");


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    todayList.add(todayListView);
                }
            }
            Collections.sort(todayList, new DocumentsComparator());
            String header = "";
            for (int i = 0; i < todayList.size(); i++) {
                if (!header.equalsIgnoreCase(todayList.get(i).uploadedDate)) {
                    header = todayList.get(i).uploadedDate;
                    todayList.get(i).setIsFirst(true);
                } else
                    todayList.get(i).setIsFirst(false);

            }


            todaysListAdapter = new TodayListAdapter(todayList, DashboardActivity.this);
            todayUpLoadedListView.setAdapter(todaysListAdapter);
            appPreferences.setCountDoc(todayList.size()+"");
            refreshDrawer();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

     /*   private void getDataFromServer(String jsonGot) {

        try {
            JSONObject jObject = new JSONObject(jsonGot);

            TodayListView tLV = new TodayListView();
            tLV.fileName = jObject.getString("documentType");
            tLV.upLoadedTime = jObject.getString("UploadedDate");
            String cerImage = jObject.getString("filePath");
            SharedPreferences preferences = getSharedPreferences(AppConstant.sharedPreferenceName, 0);
            Editor edit = preferences.edit();
            edit.putString("certImage", cerImage);
            edit.commit();
            Log.e("cerImageBinod", cerImage + "");
            ContentValues cv = new ContentValues();
            //cv.put("messageId", jObject.getString("MessageID"));
            cv.put("userType", jObject.getString("userType"));
            cv.put("userId", jObject.getString("userId"));
            //cv.put("fieldId", jObject.getString("fieldId"));
            cv.put("documentTitle", jObject.getString("documentTitle"));
            cv.put("documentType", tLV.fileName);
            cv.put("addInfo", jObject.getString("addInfo"));
            cv.put("dateOfIssue", jObject.getString("dateOfIssue"));
            cv.put("dateOfExpire", jObject.getString("dateOfExpire"));
            cv.put("suplierName", jObject.getString("suplierName"));
            cv.put("registratinId", jObject.getString("registrationId"));
            cv.put("upLoadedDate", tLV.upLoadedTime);
            cv.put("empImage", cerImage);

            Log.e("contentValue", cv + "");
            if (Integer.parseInt(reciveEditId) > 0) {
                DatabaseHelper.upDate(getApplicationContext(), cv, "tblDocuments", reciveEditId);
                Log.e("updateB", reciveEditId + "");
            } else if (recivReturnValue != null) {
                DatabaseHelper.insertData(getApplicationContext(), cv, "tblDocuments");
                Log.e("updateB", "insertToDatabase");
            } else {
                setInAdapterFromDataBase();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /* public void setInAdapterFromDataBase() {
         todayList = new ArrayList<TodayListView>();

         Cursor cursor = DatabaseHelper.readQuery(getApplicationContext(), "SELECT docId,documentType,upLoadedDate FROM tblDocuments ORDER BY upLoadedDate DESC ");
         todayList = new ArrayList<TodayListView>();

         if (cursor != null) {
             if (cursor.moveToFirst()) {
                 do {
                     TodayListView tlV = new TodayListView();
                  //   tlV.docId = cursor.getInt(0);
                     tlV.fileName = cursor.getString(1);
                     tlV.upLoadedTime = cursor.getString(2);
                     todayList.add(tlV);
                 } while (cursor.moveToNext());
             }
         }
         todaysListAdapter = new TodayListAdapter(todayList);
         todayUpLoadedListView.setAdapter(todaysListAdapter);

     }
 */
    public boolean isTableExists() {

        return false;

    }

    public void captureImageFromSdCard() {
        Intent intent = new Intent(
        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
    public void onBackPressed() {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
                dialog.dismiss();
            }
        });
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
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
              /*  Uri imageUri = data.getData();

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cur = managedQuery(imageUri, projection, null, null, null);
                cur.moveToFirst();
                imageFilePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));*/

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageFilePath = cursor.getString(columnIndex);
                cursor.close();

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


    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if (id == GET_DOC_LIST) {
            //  Toast.makeText(DashboardActivity.this, object, Toast.LENGTH_LONG).show();
            if (todaysListAdapter != null) {
                todayList.clear();
            }
            getDocuments(object);
        }
        else if(id==1003)
            hitAllDataApi();
        else if(id==1004)
            hitAllDataApi();
        else if (id == CREATE_FOLDER) {
            if (object != null && !object.equals("")) {
                try {
                    JSONObject main = new JSONObject(object);
                    if (main.has("status")) {
                        if (main.optString("status").equals("1")) {
                            Toast.makeText(DashboardActivity.this, "Folder created successfully", Toast.LENGTH_LONG).show();
                            hitAllDataApi();
                            // we will notify the adapter

                        } else
                            Toast.makeText(DashboardActivity.this, "Error while creating folder, please try again later", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(DashboardActivity.this, "Error while creating folder, please try again later", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    protected void onResume() {
        hitAllDataApi();
        super.onResume();
    }
}

