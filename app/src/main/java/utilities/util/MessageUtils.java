package utilities.util;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;


import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Locale;

public class MessageUtils {

    private static final boolean isDEBUG = true ;

    public static String hex2String(String hex){
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for( int i=0; i<hex.length()-1; i+=2 ){
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static String padleft(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len) {
            return null;
        }
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0) {
            d.append(c);
        }
        d.append(s);
        return d.toString();
    }

    public static String string2Hex(String str) {
        if(str == null || str.equals("")){
            return null ;
        }
        byte[] byteArr = str.getBytes() ;
        StringBuffer strBufTemp = new StringBuffer("");
        for (int i = 0; i < byteArr.length; i++) {
            String stmp = Integer.toHexString(byteArr[i] & 0XFF);
            if (stmp.length() == 1) {
                strBufTemp.append("0" + stmp);
            }else {
                strBufTemp.append(stmp);
            }
        }
        return strBufTemp.toString().toUpperCase(Locale.getDefault());
    }

    public static String byte2HexStr(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            }else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0" + s);
        }
    }

    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            // Buginfo when i oddness then this line won't be work
            // but in the for judge i>0 & i++ so i absolutely won't be oddness
            int shift = ((i % 2 == 1) ? 0 : 4);
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }

    public static String calCRC(byte[] data) {
        byte[] buf = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;
        int crc = 0x0000;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0x8005
                    crc ^= 0x8005;
                } else {
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
                }
            }
        }
        String c = Integer.toHexString(crc);
        if (c.length() == 3) {
            c = "0" + c;
        } else if (c.length() == 2) {
            c = "00" + c;
        }
        return c;
    }

    public static byte[] str2bcd(String s, boolean padLeft) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        byte[] d = new byte[len + 1 >> 1];
        return str2bcd(s, padLeft, d, 0);
    }

    /**
     * byte 2 int
     * @param bytes
     * @return
     */
    public static int byte2int(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4 - bytes.length; i++) {
            byteBuffer.put((byte) 0);
        }
        for (int i = 0; i < bytes.length; i++) {
            byteBuffer.put(bytes[i]);
        }
        byteBuffer.position(0);
        return byteBuffer.getInt();
    }

    public static byte[] str2bcd(String s, boolean padLeft, byte[] d, int offset) {
        char c;
        int len = s.length();
        int start = (len & 1) == 1 && padLeft ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            c = s.charAt(i - start);
            if (c >= '0' && c <= '?') {
                c -= '0';
            }else {
                c &= ~0x20;
                c -= 'A' - 10;
            }
            d[offset + (i >> 1)] |= c << ((i & 1) == 1 ? 0 : 4);
        }
        return d;
    }

    /**
     * 注入主密钥
     * @return
     */
//    private static int loadKeys(int index , String keys){
//        debug("loadKeys->index:"+index+",keys:"+keys);
//        return Ped.getInstance().injectKey(KeySystem.MS_DES ,
//                KeyType.KEY_TYPE_MASTK ,
//                index ,
//                str2bcd(keys , false)) ;
//    }



    /**
     * 是否有设备接入
     * @param context
     * @return
     */
    public static boolean hasUSB(Context context){
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        return deviceList.size() > 0 ? true : false ;
    }

    public static void debug(String msg){
        if(isDEBUG){
            Log.w("MPOS" , msg) ;
        }
    }
}
