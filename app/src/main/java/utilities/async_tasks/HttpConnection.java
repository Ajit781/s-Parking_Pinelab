

package utilities.async_tasks;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.util.ArrayList;

import javax.net.ssl.SSLException;


public class HttpConnection implements HttpRequestRetryHandler
{
    HttpClient client ;
    HttpPost post;
    InputStream is;

	/**
	 * This method make the web connection using {@link HttpPost} method and get
	 * the data in String format from the web
	 *
	 * @param url
	 *            The web service url passed as String
	 * @param list
	 *            The parrameter passed in the service in {@link java.util.List} of
	 *            {@link NameValuePair}
	 * @return {@link String} the return from the web service
	 */
	protected String getPostRespoonse(String url, ArrayList<NameValuePair> list)
	{
        String res = "";
        try
        {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 40000);
            HttpConnectionParams.setSoTimeout(httpParameters, 40000);

            client = new DefaultHttpClient(httpParameters);

            post = new HttpPost(url);
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            if( list!=null ) {
                post.setEntity(new UrlEncodedFormEntity(list));
            }

            HttpResponse response = client.execute(post);
            StatusLine status = response.getStatusLine();
            Log.e(url + "=", String.valueOf(status.getStatusCode()));
            if(status.getStatusCode() == 200)
            {
                res = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }
            else
            {
                res = HttpStatusHandeling.getJsonObject(status.getStatusCode());
            }

        }
        catch(Exception e)
        {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject("{\"status\":\"2\",\"msg\":\"Network issue occured, Please try again.\"}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            res = jsonObject.toString();
        }

        return res;
    }


	@Override
	public boolean retryRequest(IOException exception, int retryCount, HttpContext hContext)
	{

		if(retryCount >= 5)
			return false;
		else
		{
			if(exception instanceof ConnectException)
				return true;
			else if(exception instanceof SSLException)
				return true;
			else if(exception instanceof InterruptedIOException)
				return true;
			else
				return false;
		}
	}
}