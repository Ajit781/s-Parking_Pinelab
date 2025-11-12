
package utilities.async_tasks;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlconnectionWithJsonParsing {
    String result = "";
    final int CONNECTION_TIMEOUT = 40000;
    final int DATARETRIEVAL_TIMEOUT = 40000;
    private final String USER_AGENT = "Mozilla/5.0";

    public String getPostresponse(String targetUrl, String param){
        URL url;
        HttpURLConnection con = null;
        try{
            //create connection
            url = new URL(targetUrl);
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(DATARETRIEVAL_TIMEOUT);

        /*    con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");*/
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            // if the parameter length is known then we usse following line otherwisw we use con.setChunkedStreamingMode(0);
            con.setFixedLengthStreamingMode(param.getBytes().length);
            // or the following lne
            /*con.setRequestProperty("Content-Length", "" +
                    Integer.toString(param.getBytes().length));*/
            //con.setRequestProperty("Content-Language", "en-US");

            con.setUseCaches(false);
            con.setDoInput(true);
            // initially it is get method. setDoOutput(true) means wr are using post method now.
            con.setDoOutput(true);

            //send request
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(param);
            wr.flush();
            wr.close();

            // handle issues
            int statusCode = con.getResponseCode();
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

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject("{\"status\":\"2\",\"message\":\"Could not connect to the server.\"}");
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