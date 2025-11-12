package utilities.retrofit;


import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

public class RetrofitError {

    public static String codeToErrorMessage(int code)
    {
        if((code>=400) && (code<500))
            return String.valueOf("Server Issue");
        else if((code>=400) && (code<500))
            return String.valueOf("Server Issue");

        else
            return String.valueOf("Unresolve Server Issue");
    }

    public static String showErrorMessage(Throwable error) {
        if (error instanceof SocketTimeoutException)
        {
            return String.valueOf("No Internet Connection");
        }
        else if (error instanceof UnknownHostException)
        {
            return String.valueOf("Server Issue");
        }
        else if (error instanceof HttpException) {
            int code =((HttpException) error).code();
            String sms =codeToErrorMessage(code);
            return sms;
        }
        else
        {
            return String.valueOf("Unresolve Server Issue");
        }
    }
}