package vis.com.au.wallte.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vis.com.au.Utility.AppConstant;
import vis.com.au.activity.FolderList;
import vis.com.au.helper.NetworkTask;
import vis.com.au.wallte.R;

/**
 * Created by SAZID ALI on 17/02/2016.
 */
public class ManageFolderActivity extends Activity implements View.OnClickListener,NetworkTask.Result{

    private TextView txtRename,txtDelete;
    private ImageView backBtn,addFolder;
    private NetworkTask networkTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_folder);
        txtDelete=(TextView)findViewById(R.id.txtdelete);
        txtRename=(TextView)findViewById(R.id.txtrename);
        backBtn=(ImageView)findViewById(R.id.back_button);
        addFolder=(ImageView)findViewById(R.id.add_folder);
        addFolder.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        txtRename.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.txtrename:
                Intent intent=new Intent(ManageFolderActivity.this, FolderList.class);
                intent.putExtra("type",true);
                startActivity(intent);
                //Toast.makeText(ManageFolderActivity.this,"click on rename folder option 51168205649",Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtdelete:
                Intent intent2=new Intent(ManageFolderActivity.this, FolderList.class);
                intent2.putExtra("type",false);
                startActivity(intent2);      //Toast.makeText(ManageFolderActivity.this,"click on delete folder option",Toast.LENGTH_SHORT).show();
                break;
            case R.id.back_button:
                finish();
                break;
            case R.id.add_folder:
                createFolderDialog();
                break;
        }
    }

    private void createFolderDialog() {

        final Dialog dialog = new Dialog(this);
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
                    networkTask = new NetworkTask(ManageFolderActivity.this, 1005, listValue);
                    networkTask.exposePostExecute(ManageFolderActivity.this);
                    networkTask.execute("http://workerswallet.com.au/walletapi/paid_basic.php");

                } else
                    Toast.makeText(ManageFolderActivity.this, "Folder name can't be empty", Toast.LENGTH_LONG).show();

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
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if (id == 1005) {
            if (object != null && !object.equals("")) {
                try {
                    JSONObject main = new JSONObject(object);
                    if (main.has("status")) {
                        if (main.optString("status").equals("1")) {
                            Toast.makeText(ManageFolderActivity.this, "Folder created successfully", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(ManageFolderActivity.this, "Error while creating folder, please try again later", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(ManageFolderActivity.this, "Error while creating folder, please try again later", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    }
