package internal;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 08.03.2016.
 */
public class base58 {
    private static final char[] base58chars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final BigInteger number58 = new BigInteger("58");

    public static String toBase58(byte[] bytes) {
        if (bytes == null) return "";
        if (bytes.length == 0) return "";
        int leadigZeros = 0;
        while (leadigZeros < bytes.length) {
            if (bytes[leadigZeros] != 0) break;
            leadigZeros++;
        }
        if ((bytes.length - leadigZeros) < 1) return "";
        byte[] tocalculate = new byte[(bytes.length - leadigZeros) + 1];
        tocalculate[0] = 0;
        System.arraycopy(bytes, leadigZeros, tocalculate, 1, tocalculate.length - 1);
        StringBuilder output = new StringBuilder();
        BigInteger calculator = new BigInteger(tocalculate);
        while (!calculator.equals(BigInteger.ZERO)) {
            output.append(base58chars[calculator.mod(number58).intValue()]);
            calculator = calculator.divide(number58);
        }
        while (leadigZeros > 0) {
            output.append(base58chars[0]);
            leadigZeros--;
        }
        return output.reverse().toString();
    }

    public static byte[] fromBase58(String encoded) {
        if (encoded == null) return null;
        if (encoded.length() == 0) return null;
        int leadigZeros = 0;
        while (leadigZeros < encoded.length()) {
            if (encoded.charAt(leadigZeros) != base58chars[0]) break;
            leadigZeros++;
        }

        HashMap<Character, Long> characterMap = new HashMap<Character, Long>();
        for (int i = 0; i < base58chars.length; i++) {
            characterMap.put(base58chars[i], (long) i);
        }

        BigInteger calculator = new BigInteger("0");
        for (int i = leadigZeros; i < encoded.length(); i++) {
            if (!characterMap.containsKey(encoded.charAt(i))) return null;
            calculator = calculator.multiply(number58).add(BigInteger.valueOf(characterMap.get(encoded.charAt(i))));
        }
        int correctionZero = 0;
        byte[] bytes = calculator.toByteArray();
        while (correctionZero < bytes.length) {
            if (bytes[correctionZero] != 0) break;
            correctionZero++;
        }
        byte[] output = new byte[(bytes.length - correctionZero) + leadigZeros];
        System.arraycopy(bytes, correctionZero, output, leadigZeros, (output.length - leadigZeros));
        while (leadigZeros > 0) {
            leadigZeros--;
            output[leadigZeros] = 0;
        }
        return output;
    }
}
