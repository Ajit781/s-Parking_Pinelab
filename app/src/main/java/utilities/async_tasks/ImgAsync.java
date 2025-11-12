

package utilities.async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class ImgAsync extends AsyncTask<ArrayList<NameValuePair>, Void, String> {
    private String string_JSON;

    /**
     * Creating an instance of the {@link AsyncResponse} interface to receive
     * {@code processFinish()}
     */
    public AsyncResponse delegate = null;
    private String url;
    public String type = "";


    // Constants
    public static final String EDIT_CUSTOMER_PROFILE= "editcustomerprofile";
    public static final String USERUPDATEIMAGE = "userUpdateImage";
    public static final String SETUSEDCARIMAGEUPLOAD = "SetUsedCarImageUpload";

    /**
     * Pass the Url of the web service as a String
     * <p/>
     * url
     */
    public ImgAsync(String url) {
        Log.e("SERVICE URL # ", url);
        this.url = url;
    }

    @Override
    protected String doInBackground(@SuppressWarnings("unchecked") ArrayList<NameValuePair>... pairs) {



        String result="";

        if(type == SETUSEDCARIMAGEUPLOAD /*|| type == CHANGEPROFILEIMAGE||type == ADDUSERPHOTOS*/){
            MultiPartFileUpload multiPart_fileUpload=new MultiPartFileUpload();
            return multiPart_fileUpload.makeServiceCall(url, pairs[0]);
        } else{
            HttpConnection connection = new HttpConnection();
            string_JSON = connection.getPostRespoonse(url, pairs[0]);
        }


        return string_JSON;
        //return result;


    }

    @Override
    protected void onPostExecute(String jsonString) {
        delegate.processFinish(type, jsonString);
    }



}
