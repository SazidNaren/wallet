package vis.com.au.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import vis.com.au.Utility.AppConstant;
import vis.com.au.Utility.TodayListView;
import vis.com.au.adapter.TodayListAdapter;
import vis.com.au.helper.DocumentsComparator;
import vis.com.au.helper.NetworkTask;
import vis.com.au.wallte.R;

/**
 * Created by SAZID ALI on 08/03/2016.
 */
public class FolderList extends ActionBarActivity implements NetworkTask.Result {
    private ListView listView;
    private NetworkTask networkTask;
    private TodayListAdapter todaysListAdapter;
    private List<TodayListView> todayList;
    private boolean isRename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_list);
        setViews();
        listView=(ListView)findViewById(R.id.folder_list);
        hitAllDataApi();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isRename)
                    createFolderDialog(position);
                    else
                    deleteFolder(position);
            }
        });
    }

    private void deleteFolder(int position) {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "DeleteFolder"));
        listValue.add(new BasicNameValuePair("folderId", Integer.toString(Integer.parseInt(todayList.get(position).getDocId()))));
        networkTask = new NetworkTask(FolderList.this, 1003, listValue);
        networkTask.exposePostExecute(FolderList.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
    }
    private void renameFolder(String s, int position) {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "RenameFolder"));
        listValue.add(new BasicNameValuePair("folderName", s));
        listValue.add(new BasicNameValuePair("folderId", Integer.toString(Integer.parseInt(todayList.get(position).getDocId()))));
        networkTask = new NetworkTask(FolderList.this, 1004, listValue);
        networkTask.exposePostExecute(FolderList.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
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
                    Toast.makeText(FolderList.this, "Folder name can't be empty", Toast.LENGTH_LONG).show();

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





    private void setViews() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        if(getIntent().hasExtra("type"))
            isRename=getIntent().getBooleanExtra("type",false);
        if(isRename)
            getSupportActionBar().setTitle("Rename Folder");
        else
            getSupportActionBar().setTitle("Delete Folder");
    }

    private void hitAllDataApi() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("actions", "getAllDocFolder"));
        listValue.add(new BasicNameValuePair("Type", "1"));
        listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("fetch", "All"));

        networkTask = new NetworkTask(FolderList.this, 1002, listValue);
        networkTask.exposePostExecute(FolderList.this);
        networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");
    }

    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if(id==1002)
        {
            getDocuments(object);
        } else if(id==1003)
            hitAllDataApi();
        else if(id==1004)
            hitAllDataApi();

    }

    private void getDocuments(String object) {
            try {
                todayList=new ArrayList<>();
                JSONArray jsonArray = new JSONArray(object);
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
                        } else if (i == 1) {
                            JSONObject child = jsonSubArray.getJSONObject(j);
                            todayListView.setDocId(child.getString("doc_id"));
                            todayListView.setFileName(child.getString("file_title"));
                            todayListView.setFilePath(child.getString("file_path"));
                            todayListView.setIsFolder(false);
                        }

                        todayList.add(todayListView);
                    }
                }
               /* Collections.sort(todayList, new DocumentsComparator());
                String header = "";
                for (int i = 0; i < todayList.size(); i++) {
                    if (!header.equalsIgnoreCase(todayList.get(i).uploadedDate)) {
                        header = todayList.get(i).uploadedDate;
                        todayList.get(i).setIsFirst(true);
                    } else
                        todayList.get(i).setIsFirst(false);

                }*/
                todaysListAdapter = new TodayListAdapter(todayList, FolderList.this);
                listView.setAdapter(todaysListAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


