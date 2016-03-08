package internal;

import java.util.HashMap;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 08.03.2016.
 */
public class hex {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] hexAppend(byte[] first, byte[] second) {
        if ((first == null) || (second == null)) return null;
        byte[] output = new byte[first.length + second.length];
        System.arraycopy(first, 0, output, 0, first.length);
        System.arraycopy(second, 0, output, first.length, second.length);
        return output;
    }

    public static HashMap<Integer, byte[]> hexSeparate(byte[] toSeparate, int length) {
        if (toSeparate == null) return null;
        if (length == 0) return null;
        if (length > toSeparate.length) length = toSeparate.length;
        byte[] firstOut = new byte[length];
        System.arraycopy(toSeparate, 0, firstOut, 0, firstOut.length);
        byte[] secondOut = new byte[toSeparate.length - length];
        if (secondOut.length > 0) {
            System.arraycopy(toSeparate, length, secondOut, 0, secondOut.length);
        }
        HashMap out = new HashMap<Integer, byte[]>();
        out.put(0, firstOut);
        out.put(1, secondOut);
        return out;
    }
}
