package vis.com.au.wallte.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.database.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import stripe.example.PaymentForm;
import stripe.example.TokenList;
import stripe.example.dialog.ErrorDialogFragment;
import stripe.example.dialog.ProgressDialogFragment;
import vis.com.au.Utility.AppConstant;
import vis.com.au.apppreferences.AppPreferences;
import vis.com.au.helper.NetworkTask;


public class PaymentActivity extends FragmentActivity implements NetworkTask.Result{

    private NetworkTask networkTask;
    private AppPreferences appPreferences;
    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */
    public static final String PUBLISHABLE_KEY = "pk_test_6pRNASCoBOKtIshFeQd4XMUh";

    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        appPreferences=AppPreferences.getInstance(this);
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    }

    public void saveCreditCard(PaymentForm form) {

        Card card = new Card(
                form.getCardNumber(),
                form.getExpMonth(),
                form.getExpYear(),
                form.getCvc());
        card.setCurrency(form.getCurrency());

        boolean validation = card.validateCard();
        if (validation) {
            startProgress();
            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                    public void onSuccess(Token token) {
                        getTokenList().addToList(token);
                        changeToPaidVersion();
                    /*    FullWallet fullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                        String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
                        com.stripe.model.Token token2 = com.stripe.model.Token.GSON.fromJson(tokenJSON, com.stripe.model.Token.class);*/

                        finishProgress();
                        }
                    public void onError(Exception error) {
                            handleError(error.getLocalizedMessage());
                            finishProgress();
                        }
                    });
        } else if (!card.validateNumber()) {
        	handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
        	handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
        	handleError("The CVC code that you entered is invalid");
        } else {
        	handleError("The card details that you entered are invalid");
        }
    }

    private void changeToPaidVersion() {
        List<NameValuePair> listValue = new ArrayList<NameValuePair>();
        //listValue.add(new BasicNameValuePair("userId", getSharedPreferences(AppConstant.sharedPreferenceName, 0).getString("empId", "")));
        listValue.add(new BasicNameValuePair("Id",appPreferences.getEmp_id()));
        listValue.add(new BasicNameValuePair("action", "updateToPaidVersion"));
        listValue.add(new BasicNameValuePair("type", "employee"));
        listValue.add(new BasicNameValuePair("email",appPreferences.getEmail()));
        listValue.add(new BasicNameValuePair("paid", Integer.toString(1)));
        networkTask = new NetworkTask(PaymentActivity.this, 1, listValue);
        networkTask.exposePostExecute(PaymentActivity.this);
        networkTask.execute(AppConstant.updateToPaidVersion);
    }

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }

    private TokenList getTokenList() {
        return (TokenList)(getSupportFragmentManager().findFragmentById(R.id.token_list));
    }

    @Override
    public void resultFromNetwork(String object, int id, Object arg1, Object arg2) {
        if(object!=null && !object.equals(""))
        {
            try {

                JSONObject jsonObject = new JSONObject(object);
                if (jsonObject.optString("MessageID").equals("7683")) {
                    Intent intent=new Intent(PaymentActivity.this,LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    appPreferences.getEditor().clear().commit();
                    SharedPreferences loginSharedPreferences;
                    loginSharedPreferences = getSharedPreferences(AppConstant.sharedPreferenceName, 0);
                    SharedPreferences.Editor editor = loginSharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Toast.makeText(PaymentActivity.this,jsonObject.optString("Message"),Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
