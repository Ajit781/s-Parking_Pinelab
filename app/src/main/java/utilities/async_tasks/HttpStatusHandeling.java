
package utilities.async_tasks;

import android.util.Log;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class HttpStatusHandeling
{
    /**
     * This will give the type off status code and the corresponding message in a json format String.
     *
     * @param type The type of http status code.
     * @return Json format String
     * @throws Exception
     */
    public static String getJsonObject(int type) throws Exception
    {
        JSONObject json = null;

        switch (type)
        {
            //202
            case HttpURLConnection.HTTP_ACCEPTED:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_ACCEPTED\"}");
                Log.i("http errror status:","202:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //200
            case HttpURLConnection.HTTP_OK:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_OK\"}");
                Log.i("http errror status:","200:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //502
            case HttpURLConnection.HTTP_BAD_GATEWAY:
               // json = new JSONObject("{\"status\":\"2\",\"message\":\"BAD_GAteWay\"}");
                Log.i("http errror status:","502:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //400
            case HttpURLConnection.HTTP_BAD_REQUEST:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_BAD_REQUEST\"}");
                Log.i("http errror status:","400:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //408
            case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_CLIENT_TIMEOUT\"}");
                Log.i("http errror status:","408:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //409
            case HttpURLConnection.HTTP_CONFLICT:
               // json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_CONFLICT\"}");
                Log.i("http errror status:","409:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //413
            case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_ENTITY_TOO_LARGE\"}");
                Log.i("http errror status:","413:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //403
            case HttpURLConnection.HTTP_FORBIDDEN:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_FORBIDDEN\"}");
                Log.i("http errror status:","403:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //504
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_GATEWAY_TIMEOUT\"}");
                Log.i("http errror status:","504:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //410
            case HttpURLConnection.HTTP_GONE:
               // json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_GONE\"}");
                Log.i("http errror status:","410:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //500
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
             //  json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_INTERNAL_ERROR\"}");
                Log.i("http errror status:","500:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"7\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //411
            case HttpURLConnection.HTTP_LENGTH_REQUIRED:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_LENGTH_REQUIRED\"}");
                Log.i("http errror status:","411:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //301
            case HttpURLConnection.HTTP_MOVED_PERM:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_MOVED_PERM\"}");
                Log.i("http errror status:","301:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //302
            case HttpURLConnection.HTTP_MOVED_TEMP:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_MOVED_TEMP\"}");
                Log.i("http errror status:","302:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //300
            case HttpURLConnection.HTTP_MULT_CHOICE:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_MULT_CHOICE\"}");
                Log.i("http errror status:","300:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //204
            case HttpURLConnection.HTTP_NO_CONTENT:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_NO_CONTENT\"}");
                Log.i("http errror status:","204:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //406
            case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_NOT_ACCEPTABLE\"}");
                Log.i("http errror status:","406:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //203
            case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
               // json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_NOT_AUTHORITATIVE\"}");
                Log.i("http errror status:","203:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;
/***************************************************************/

            //404
            case HttpURLConnection.HTTP_NOT_FOUND:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_NOT_FOUND\"}");
                Log.i("http errror status:","404:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //501
            case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_NOT_IMPLEMENTED\"}");
                Log.i("http errror status:","501:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //304
            case HttpURLConnection.HTTP_NOT_MODIFIED:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_NOT_MODIFIED\"}");
                Log.i("http errror status:","304:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //206
            case HttpURLConnection.HTTP_PARTIAL:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_PARTIAL\"}");
                Log.i("http errror status:","206:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //402
            case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_PAYMENT_REQUIRED\"}");
                Log.i("http errror status:","402:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //412
            case HttpURLConnection.HTTP_PRECON_FAILED:
               // json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_PRECON_FAILED\"}");
                Log.i("http errror status:","412:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //407
            case HttpURLConnection.HTTP_PROXY_AUTH:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_PROXY_AUTH\"}");
                Log.i("http errror status:","407:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //414
            case HttpURLConnection.HTTP_REQ_TOO_LONG:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_REQ_TOO_LONG\"}");
                Log.i("http errror status:","414:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //205
            case HttpURLConnection.HTTP_RESET:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_RESET\"}");
                Log.i("http errror status:","205:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //303
            case HttpURLConnection.HTTP_SEE_OTHER:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_SEE_OTHER\"}");
                Log.i("http errror status:","303:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;
/*			case HttpURLConnection.HTTP_S:
				json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_SERVER_ERROR\"}");
				break;*/

            //401
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_UNAUTHORIZED\"}");
                Log.i("http errror status:","401:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //503
            case HttpURLConnection.HTTP_UNAVAILABLE:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_UNAVAILABLE\"}");
                Log.i("http errror status:","503:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //415
            case HttpURLConnection.HTTP_UNSUPPORTED_TYPE:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_UNSUPPORTED_TYPE\"}");
                Log.i("http errror status:","415:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //305
            case HttpURLConnection.HTTP_USE_PROXY:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_USE_PROXY\"}");
                Log.i("http errror status:","305:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;

            //505
            case HttpURLConnection.HTTP_VERSION:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_VERSION\"}");
                Log.i("http errror status:","505:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;
            //429
            case 429:
                //json = new JSONObject("{\"status\":\"2\",\"message\":\"HTTP_VERSION\"}");
                Log.i("http errror status:","429:Network issue occured, Please try again.");
                json = new JSONObject("{\"status\":\"2\",\"message\":\"Network issue occured, Please try again.\"}");
                break;
            default:
                break;
        }

        return json.toString();
    }

}
