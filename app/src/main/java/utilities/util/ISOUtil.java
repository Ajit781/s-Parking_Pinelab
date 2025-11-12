package utilities.util;

/**
 * Author: guojianwei
 * Description:
 */
public class ISOUtil {

    public static String padleft(String s, int len, char c) {
        if (s==null) return null;
        s = s.trim();
        if (s.length() > len)
            return null;
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0)
            d.append(c);
        d.append(s);
        return d.toString();
    }
}
