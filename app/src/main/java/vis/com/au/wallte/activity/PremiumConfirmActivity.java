package vis.com.au.wallte.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import vis.com.au.wallte.R;

/**
 * Created by Linchpin25 on 2/14/2016.
 */
public class PremiumConfirmActivity extends Activity implements View.OnClickListener
{
    TextView tvCancel,tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premium_confirm_activity);
        tvCancel= (TextView) findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        tvConfirm= (TextView) findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvCancel:
                finish();
                break;
            case R.id.tvConfirm:
                startActivity(new Intent(PremiumConfirmActivity.this, PaymentActivity.class));
                break;

        }

    }
}
