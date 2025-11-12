

package utilities.async_tasks;

public interface AsyncResponse
{
    /**
     * This method return the result from the web service response.
     */
    void processFinish(String type, String output);
}
