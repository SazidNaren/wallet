package vis.com.au.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vis.com.au.Utility.AppConstant;
import vis.com.au.Utility.TodayListView;
import vis.com.au.adapter.TodayListAdapter;
import vis.com.au.helper.NetworkTask;
import vis.com.au.wallte.R;
import vis.com.au.wallte.activity.DisplayUserDetails_Activity;

/**
 * Created by SAZID ALI on 08/03/2016.
 */
public class NotificationActivity extends AppCompatActivity implements NetworkTask.Result {
    private ListView listView;
    private NetworkTask networkTask;
    private TodayListAdapter todaysListAdapter;
    private List<TodayListView> todayList;

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
                Intent i = new Intent(NotificationActivity.this, DisplayUserDetails_Activity.class);
                i.putExtra("docId", todayList.get(position).docId);
                i.putExtra("docName", todayList.get(position).getFileName());
                startActivity(i);
            }
        });
    }
    private void setViews() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fab411")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.back_white);
        getSupportActionBar().setTitle("Notification");
    }

    private void hitAllDataApi() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();

        listValue.add(new BasicNameValuePair("actions", "getAllDocFolder"));
        listValue.add(new BasicNameValuePair("Type", "1"));
        listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("fetch", "All"));
        networkTask = new NetworkTask(NotificationActivity.this, 1002, listValue);
        networkTask.exposePostExecute(NotificationActivity.this);
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
                        JSONObject child = jsonSubArray.getJSONObject(j);
                        if (i == 1) {
                            todayListView.setDocId(child.getString("doc_id"));
                            todayListView.setFileName(child.getString("file_title"));
                            todayListView.setFilePath(child.getString("file_path"));
                            todayListView.setIsFolder(false);
                            if(isDateExpire(child.optString("created_date")))
                                todayList.add(todayListView);
                        }
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
                todaysListAdapter = new TodayListAdapter(todayList, NotificationActivity.this);
                listView.setAdapter(todaysListAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    private boolean isDateExpire(String created_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date createdDate=sdf.parse(created_date);
            Date today=new Date();
            int countBetDays=today.compareTo(createdDate);
            if(countBetDays>=15)
                return false;
            else
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}


