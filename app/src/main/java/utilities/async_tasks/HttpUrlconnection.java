
package utilities.async_tasks;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import data_objects.SParkingAgentModel;
import okhttp3.OkHttpClient;

public class HttpUrlconnection {
    String result = "";
    final int CONNECTION_TIMEOUT = 40000;
    final int DATARETRIEVAL_TIMEOUT = 40000;
    private final String USER_AGENT = "Mozilla/5.0";
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    public String getPostresponse(String targetUrl, String param){
        URL url;
        HttpURLConnection con = null;
        try{
           /* OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build();*/
            //create connection
            url = new URL(targetUrl);
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            if(dataModel.base_token.equals("")){
                con.setRequestProperty("Authorization", "Basic bW9iaWxlOnBpbg==");
            }else {
                con.setRequestProperty("Authorization", "Bearer " + dataModel.base_token);
            }
            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // if the parameter length is known then we usse following line otherwisw we use con.setChunkedStreamingMode(0);
            con.setFixedLengthStreamingMode(param.getBytes().length);
            Log.e("after param", "after line 37");
            // or the following lne
            /*con.setRequestProperty("Content-Length", "" +
                    Integer.toString(param.getBytes().length));*/
            //con.setRequestProperty("Content-Language", "en-US");

            con.setUseCaches(false);
            con.setDoInput(true);
            // initially it is get method. setDoOutput(true) means wr are using post method now.
            con.setDoOutput(true);
            Log.e("after param", "after line 47");

            //send request
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(param);
            wr.flush();
            wr.close();
            //send request
            /*BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
            wr.write(param);
            wr.flush();
            wr.close();*/

            Log.e("after param", "after line 55");

            Log.e("error", String.valueOf(con.getErrorStream()));
            // handle issues
            int statusCode = con.getResponseCode();
            Log.e("statuscode", String.valueOf(statusCode));
            //Crashlytics.log(Log.ERROR,"SParkingAgent_status",String.valueOf(statusCode));
            Log.e("after param", "after line 37");
            //Crashlytics.log(Log.ERROR,"SParkingAgent_httpurl","after line 37");

            //Log.e("resp", String.valueOf(con.getInputStream()));
            if (statusCode == 200) {
                //Get response
                InputStream in = con.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                result = response.toString();
            }else {
                result = HttpStatusHandeling.getJsonObject(statusCode);
            }
        }catch (Exception e){
            e.printStackTrace();

           // Crashlytics.log(Log.ERROR,"SParkingAgent_httpurl_exception",e.getMessage());
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            result = jsonObject.toString();
        }finally {
            if (con != null){
                con.disconnect();
            }
        }
        return result;
    }

    public String getGetresponse(String targetUrl,String param){



        // optional default is GET
        try {
            URL obj = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + targetUrl);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            result = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
}
