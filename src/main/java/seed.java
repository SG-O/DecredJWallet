import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 04.03.2016.
 */
public class seed {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    final protected static JSONArray pgpwl = getWordList();

    private BigInteger internalSeed;

    public seed(BigInteger internalSeed) {
        this.internalSeed = internalSeed;
    }

    public seed(String seedString, boolean hashed) throws seedException {
        seedString = seedString.trim();
        if (seedString.length() == 0) throw new seedException(seedException.EMPTY_SEED);
        if (seedString.contains(" ")) {
            if (pgpwl == null) {
                throw new seedException(seedException.PGP_WORDLIST_NOT_LOADED);
            }
            HashMap<String, Integer> map = new HashMap<String, Integer>(256);
            String[] words = seedString.split("\\s+");
            if (words.length == 0) throw new seedException(seedException.EMPTY_SEED);
            byte[] values = new byte[words.length];
            if (hashed) {
                values = new byte[words.length - 1];
            }
            for (int i = 0; i < pgpwl.length(); i++) {
                map.put(pgpwl.getJSONArray(i).getString(0), i);
                map.put(pgpwl.getJSONArray(i).getString(1), i);
            }
            for (int i = 0; i < values.length; i++) {
                if (!map.containsKey(words[i])) {
                    throw new seedException(seedException.INVALID_WORD, (long) i, words[i]);
                }
                values[i] = (byte) (map.get(words[i]).intValue());
            }
            this.internalSeed = new BigInteger(values);
            if (hashed) {
                if (!map.containsKey(words[words.length - 1])) {
                    throw new seedException(seedException.INVALID_WORD, (long) words.length - 1, words[words.length - 1]);
                }
                byte should = hash().toByteArray()[0];
                byte is = (byte) (map.get(words[words.length - 1]).intValue());

                if (should != is) {
                    throw new seedException(seedException.INVALID_HASH, words.length - 1, words[words.length - 1], pgpHash());
                }
            }
        } else {
            this.internalSeed = new BigInteger(hexToBytes(seedString));
        }
    }

    public seed() {
        this(256);
    }

    public seed(int strength) {
        int byteLength = 32;
        if ((strength >= 128) && (strength <= 512)) {
            byteLength = strength / 8;
        }
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[byteLength];
        random.nextBytes(bytes);
        this.internalSeed = new BigInteger(bytes);
    }

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

    private static JSONArray getWordList() {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(seed.class.getResourceAsStream("pgpwl.json")));
        StringBuilder builder = new StringBuilder();
        String read;
        try {
            while ((read = inputStream.readLine()) != null) {
                builder.append(read);
            }
            JSONArray tmpArray = new JSONArray(builder.toString());
            if (tmpArray.length() != 256) return null;
            return tmpArray;
        } catch (Exception e) {
            return null;
        }
    }

    public BigInteger getSeed() {
        return internalSeed;
    }

    @Override
    public String toString() {
        return bytesToHex(internalSeed.toByteArray());
    }

    public BigInteger hash() throws seedException {
        if (internalSeed == null) throw new seedException(seedException.EMPTY_SEED);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(internalSeed.toByteArray());
            MessageDigest secondMd = MessageDigest.getInstance("SHA-256");
            secondMd.update(md.digest());
            return new BigInteger(secondMd.digest());
        } catch (Exception e) {
            throw new seedException(seedException.UNABLE_TO_HASH);
        }
    }

    public String toPGP() throws seedException {
        if (pgpwl == null) throw new seedException(seedException.EMPTY_SEED);
        StringBuilder output = new StringBuilder();
        byte[] values = internalSeed.toByteArray();
        for (int i = 0; i < values.length; i++) {
            int v = values[i] & 0xFF;

            output.append(pgpwl.getJSONArray(v).getString(i % 2)).append(" ");
        }
        return output.toString().trim();
    }

    public String pgpHash() throws seedException {
        int check = hash().toByteArray()[0] & 0xFF;
        String message = toPGP();
        int parrity = (message.split("\\s+").length) % 2;
        return pgpwl.getJSONArray(check).getString(parrity);
    }

    public String toHashedPGP() throws seedException {
        return toPGP() + " " + pgpHash();
    }
}
