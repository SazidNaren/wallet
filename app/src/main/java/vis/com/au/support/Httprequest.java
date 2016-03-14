package vis.com.au.support;

import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.util.Log;

import vis.com.au.Utility.AppConstant;

public class Httprequest {

	public static String retValue;
	public static String makeHttpRequest(String param,String theUrl)
	{
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(AppConstant.mainUrl+theUrl);
			ArrayList<NameValuePair> nvp= new ArrayList<NameValuePair>();
			nvp.add(new BasicNameValuePair("param",param));
		
			httpPost.setEntity(new UrlEncodedFormEntity(nvp));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			retValue = EntityUtils.toString(httpResponse.getEntity());
			
			Log.e("returnValue", retValue.toString()+"");
			return retValue;
		} catch (Exception e) {
			
			Log.e("error in "+theUrl,e.toString());
			e.printStackTrace();
		}
		return "NO";
	}
}
