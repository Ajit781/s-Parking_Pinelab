package utilities;

import java.util.regex.Pattern;

public class Validation {
    static String email;
    static Pattern pattern ;

    public static boolean validateEmail(String email)
    {
        if (email == "") {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        /*pattern = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})");
        Matcher match=pattern.matcher(email);
        if(match.matches())
        {
            return true;
        }
        else {
            return false;
        }*/
    }


    public static boolean isInteger( String input )
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }
}
