package vis.com.au.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import vis.com.au.Utility.AppConstant;
import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.helper.NetworkTask;
import vis.com.au.wallte.R;
import vis.com.au.wallte.activity.AppInfoActivity;
import android.view.View.OnClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAZID ALI on 07/03/2016.
 */
public class PromoCodeScreen  extends ActionBarActivity implements OnClickListener,NetworkTask.Result {
    EditText promoCodeEdt;
    Button applyCodeBtn, proceedBtn;
    ImageView cancelBtn;
    String deviceId = "";
    NetworkTask networkTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.promocode);
        init();
        setClickListener();
        setValues();
    }

    private void init() {
        promoCodeEdt = (EditText) findViewById(R.id.promoCodeEdit);
        applyCodeBtn = (Button) findViewById(R.id.appluCodeBtn);
        proceedBtn = (Button) findViewById(R.id.proceedBtn);
        cancelBtn = (ImageView) findViewById(R.id.cancelBtn);
    }

    private void setClickListener() {
        applyCodeBtn.setOnClickListener(this);
        proceedBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }


    private void setValues() {
        deviceId=android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.appluCodeBtn:
                hitAllDataApi();
                break;
            case R.id.proceedBtn:
              /*  Intent intent2 = new Intent(PromoCodeScreen.this, AppInfoActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);*/
                finish();
                break;
            case R.id.cancelBtn:
                finish();
                break;
        }

    }
    private void hitAllDataApi() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        listValue.add(new BasicNameValuePair("deviceID", deviceId));
        listValue.add(new BasicNameValuePair("randomCode", "promo/"+promoCodeEdt.getText().toString().trim()));
        listValue.add(new BasicNameValuePair("emailAddress", AppPreferences.getInstance(this).getEmail()));
        networkTask = new NetworkTask(PromoCodeScreen.this,1011,listValue);
        networkTask.exposePostExecute(PromoCodeScreen.this);
        networkTask.execute(AppConstant.promoCode);
    }
    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if (id == 1011)
        {
          /*  Intent intent = new Intent(PromoCodeScreen.this, AppInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
            finish();
        }
    }
}

