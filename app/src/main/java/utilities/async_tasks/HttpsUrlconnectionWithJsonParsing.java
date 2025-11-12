
package utilities.async_tasks;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import data_objects.SParkingAgentModel;

public class HttpsUrlconnectionWithJsonParsing {
    String result = "";
    final int CONNECTION_TIMEOUT = 40000;
    final int DATARETRIEVAL_TIMEOUT = 40000;
    private final String USER_AGENT = "Mozilla/5.0";
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    public String getPostresponse(String targetUrl, String param){
        URL url;
        HttpsURLConnection con = null;
        try{
            //create connection
            url = new URL(targetUrl);
            con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("POST");

            con.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            con.setDefaultSSLSocketFactory(
                    context.getSocketFactory());

            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            if(dataModel.base_token.equals("")){
                con.setRequestProperty("Authorization", "Basic bW9iaWxlOnBpbg==");
            }else {
                con.setRequestProperty("Authorization", "Bearer " + dataModel.base_token);
            }

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
                jsonObject = new JSONObject("{\"status\":\"2\",\"message\":\"Please try after some time.\"}");
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
