package internal;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 08.03.2016.
 */
public class hashWrapper {
    public static byte[] ripemd160(byte[] bytes) {
        if (bytes == null) return null;
        RIPEMD160Digest md = new RIPEMD160Digest();
        md.update(bytes, 0, bytes.length);
        byte[] output = new byte[md.getDigestSize()];
        md.doFinal(output, 0);
        return output;
    }

    public static byte[] hashSha256(byte[] bytes) {
        if (bytes == null) return null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md.update(bytes);
        return md.digest();
    }

    public static byte[] doubleHashSha256(byte[] bytes) {
        return hashSha256(hashSha256(bytes));
    }

    public static byte[] shortDoubleHashSha256(byte[] bytes) {
        byte[] output = new byte[4];
        byte[] hashed = hashSha256(hashSha256(bytes));
        if (hashed == null) return output;
        System.arraycopy(hashed, 0, output, 0, output.length);
        return output;
    }

    public static byte[] shortHashSha256(byte[] bytes) {
        byte[] output = new byte[4];
        byte[] hashed = hashSha256(bytes);
        if (hashed == null) return output;
        System.arraycopy(hashed, 0, output, 0, output.length);
        return output;
    }
}
