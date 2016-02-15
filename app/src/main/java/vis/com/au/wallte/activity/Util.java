package vis.com.au.wallte.activity;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by Linchpin25 on 1/17/2016.
 */
public class Util {


    /**
     * **********For HTTP POST request******************
     */

    public static String HTTPPostResponse(String url, List<NameValuePair> nameValuePairs) {
        String res = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            res = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return res;

    }

    public static String httpPostRaw(String url, String jsonData) {
        String response = "";
        try {
            HttpClient client = new DefaultHttpClient();
//            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            StringEntity se = new StringEntity(jsonData);

            se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            post.setEntity(se);
            HttpResponse httpresponse = client.execute(post);

            response = EntityUtils.toString(httpresponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /*public static String sendRequestImageToServer(final String url, final Map<String, String> stringParts, final Map<String, File> fileParts) {
        String serverResponse = null;
        final Charset chars = Charset.forName("UTF-8");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            if (null != stringParts && stringParts.size() > 0)
                for (Map.Entry<String, String> entry : stringParts.entrySet()) {
                    try {
                        multipartEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), chars));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            if (null != fileParts && fileParts.size() > 0)
                for (Map.Entry<String, File> entry : fileParts.entrySet()) {
                    multipartEntity.addPart(entry.getKey(), new FileBody(entry.getValue()));
                }

            httppost.setEntity(multipartEntity);

            HttpResponse response = client.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                serverResponse = EntityUtils.toString(resEntity);
            }
            return serverResponse;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return serverResponse;

    }
*/


    public static String httpPostRaw(String params,
                                     List<NameValuePair> namevalueList) {
        InputStream is = null;
        String result = "";
        try {
//            HttpClient httpclient = new DefaultHttpClient();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params);
            httpPost.setEntity(new UrlEncodedFormEntity(namevalueList));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String httpGetRaw(String url) {
        InputStream is = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            is = response.getEntity().getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

}
