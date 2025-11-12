package utilities.async_tasks;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AutoCompleteConnection
{
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	//server key
	/*public static final String API_KEY = "AIzaSyCvXXMOadaDwkxr0viZepWXEhre6XEJ-eQ";*/
	public static final String API_KEY = "AIzaSyDaK2Z1h8HqIkwfINpiBsNJItIz3jIclXo";

	/**
	 * This Static Method return an address on the basic of the String passed as the parameter.
	 * the address is generated from the Google.
	 * @param input The address String .
	 * @return	<b>Google</b> generated Address String.
	 */
	public static String autocomplete(String input) {


        String resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        
	        sb.append("?key=" + API_KEY);
            sb.append("&components=country:In");
            sb.append("&rankby=distance");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&types=geocode");
			//sb.append("&types=(cities)");
            sb.append("&sensor=false");
	        
	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }

            resultList = jsonResults.toString();

	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    return resultList;
	}

}
