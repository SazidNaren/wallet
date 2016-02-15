package vis.com.au.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import org.apache.http.NameValuePair;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import vis.com.au.wallte.R;
import vis.com.au.wallte.activity.Util;


/**
 * Created by LinchPin on 6/16/2015.
 */
public class NetworkTask extends AsyncTask<String, String, String> {
    private boolean isDoinBackground, isPostExecute, isPreExecute, isProgressDialog = true;
    private String dialogMessage = "Loading";
    private DoinBackgroung doinBackgroung;
    private Result result;
    private PreNetwork preNetwork;
    private ProgressDialog pd;
    private Context ctx;
    private int id;
    private Object arg1, arg2;
    private List<NameValuePair> nameValuePairs;
    private String jsonParams;
    private boolean imageUpload = false;
    private HashMap<String, String> stringPart;
    HashMap<String, File> fileParts;

    public NetworkTask(Context ctx, int id, String jsonParam) {
        this.ctx = ctx;
        this.id = id;
        this.jsonParams = jsonParam;
    }

    public NetworkTask(Context ctx, int id) {
        this.ctx = ctx;
        this.id = id;

    }

    public NetworkTask(Context ctx, int id, boolean imageUpload, HashMap<String, String> stringPart, HashMap<String, File> fileParts) {
        this.ctx = ctx;
        this.imageUpload = imageUpload;
        this.stringPart = stringPart;
        this.fileParts = fileParts;
        this.id=id;

    }

    public NetworkTask(Context ctx, int id, List<NameValuePair> nameValuePairs) {
        this.ctx = ctx;
        this.id = id;
        this.nameValuePairs = nameValuePairs;
    }

    public NetworkTask(Context ctx, int id, Object arg1, Object arg2) {
        this.ctx = ctx;
        this.id = id;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public void exposeDoinBackground(DoinBackgroung doinBackgroung) {
        this.isDoinBackground = true;
        this.doinBackgroung = doinBackgroung;
    }

    public void exposePostExecute(Result result) {
        this.isPostExecute = true;
        this.result = result;
    }

    public void exposePreExecute(PreNetwork preNetwork) {
        this.isPreExecute = true;
        this.preNetwork = preNetwork;
    }

    public interface DoinBackgroung {

        String doInBackground(Integer id, String... params);
    }

    public interface Result {
        void resultFromNetwork(String object, int id, Object arg1, Object arg2);
    }

    public interface PreNetwork {
        void preNetwork(int id);
    }

    @Override
    protected void onPreExecute() {
        if (isProgressDialog) {
            pd = showProgressDialog(ctx);

            WindowManager.LayoutParams lp = pd.getWindow().getAttributes();
            lp.dimAmount = 0.8f;
            pd.getWindow().setAttributes(lp);
            pd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        }
        if (isPreExecute && preNetwork != null) preNetwork.preNetwork(id);

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String responseString = null;
        if (isDoinBackground && doinBackgroung != null) {

            return doinBackgroung.doInBackground(id, params);
        } else {
            String url = params[0];
           /* if (imageUpload)
                responseString = Util.sendRequestImageToServer(url,this.stringPart,this.fileParts);
            else*/ if (this.nameValuePairs == null)
                responseString = Util.httpGetRaw(url);
            else
                responseString = Util.httpPostRaw(url, this.nameValuePairs);

            System.out.println("response::" + responseString);
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String myresult) {
        if (isProgressDialog && pd.isShowing()) pd.dismiss();
        if (isPostExecute && result != null) result.resultFromNetwork(myresult, id, arg1, arg2);
        super.onPostExecute(myresult);
    }

    public ProgressDialog showProgressDialog(Context context) {
        ProgressDialog pd = new ProgressDialog(context, R.style.TransparentProgressDialog);
        pd.setMessage(dialogMessage);
        pd.setTitle(null);
        pd.show();

        pd.setProgressStyle(R.style.TransparentProgressDialog);
        View v = View.inflate(context, R.layout.custom_progress_dialog, null);
        ((TextView) v.findViewById(R.id.lodingText)).setText("Please wait...");
        pd.setContentView(v);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        return pd;
    }

    public void setProgressDialog(boolean isProgressDialog) {
        this.isProgressDialog = isProgressDialog;
    }
}
