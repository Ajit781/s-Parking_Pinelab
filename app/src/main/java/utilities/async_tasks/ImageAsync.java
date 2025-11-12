package utilities.async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class ImageAsync extends AsyncTask<ArrayList<NameValuePair>, Void, String> {
    private String string_JSON;

    /**
     * Creating an instance of the {@link AsyncResponse} interface to receive
     * {@code processFinish()}
     */
    public AsyncResponse delegate = null;
    private String url;
    public String type = "";


    // Constants
    public static final String EDIT_PROFILE = "editprofile";
    public static final String AVATAR = "avatar";


    /**
     * Pass the Url of the web service as a String
     * <p/>
     * url
     */
    public ImageAsync(String url) {
        Log.e("SERVICE URL # ", url);
        this.url = url;
    }

    @Override
    protected String doInBackground(@SuppressWarnings("unchecked") ArrayList<NameValuePair>... pairs) {

       /* HttpConnection connection = new HttpConnection();
        string_JSON = connection.getPostRespoonse(url, pairs[0]);*/

        if(type == AVATAR /*|| type == CHANGEPROFILEIMAGE||type == ADDUSERPHOTOS*/){
            MultiPartFileUpload multiPart_fileUpload=new MultiPartFileUpload();
            return multiPart_fileUpload.makeServiceCall(url, pairs[0]);
        } else{
            HttpConnection connection = new HttpConnection();
            string_JSON = connection.getPostRespoonse(url, pairs[0]);
        }

        return string_JSON;



    }

    @Override
    protected void onPostExecute(String jsonString) {
        delegate.processFinish(type, jsonString);
    }

}
