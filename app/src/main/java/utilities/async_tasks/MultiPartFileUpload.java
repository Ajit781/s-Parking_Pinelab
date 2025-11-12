
package utilities.async_tasks;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.ArrayList;

public class MultiPartFileUpload {





    @SuppressWarnings("deprecation")
    public static String makeServiceCall(String url, ArrayList<NameValuePair> mPairs) {
        String result ="";
        try {
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpPost mHttpPost = new HttpPost(url);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (NameValuePair nameValuePair : mPairs) {

                if((nameValuePair.getName().equals("usedcar_image")||nameValuePair.getName().equals("project_files")) && !nameValuePair.getValue().equals(""))
                   multipartEntityBuilder.addBinaryBody(nameValuePair.getName(),new File(nameValuePair.getValue()), ContentType.create("image/jpg"),"test.jpg");
                    //multipartEntityBuilder.addBinaryBody(nameValuePair.getName(),new File(nameValuePair.getValue()));
                else
                    multipartEntityBuilder.addPart(nameValuePair.getName(), new StringBody((nameValuePair.getValue())));
            }
            mHttpPost.setEntity(multipartEntityBuilder.build());
            HttpResponse response = mHttpClient.execute(mHttpPost);
            if(response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
            }else
            result = "";
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }
}
