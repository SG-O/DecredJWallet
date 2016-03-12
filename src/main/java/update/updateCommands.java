package update;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 06.03.2016.
 */
public class updateCommands {
    JSONArray commands;
    String signature;
    File temp = null;

    public updateCommands(JSONObject tasks) throws updateException {
        if (!tasks.has("Commands")) throw new updateException(updateException.NO_COMMANDS);
        commands = tasks.optJSONArray("Commands");
        signature = tasks.optString("Signature", "");
    }

    public boolean checkSignature(BigInteger modulus, BigInteger pubExp) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(key);
            signature.update(commands.toString().getBytes());
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] sigBytes = decoder.decodeBuffer(this.signature);
            return signature.verify(sigBytes);
        } catch (Exception e) {
            return false;
        }
    }

    public updateItem[] parse() throws updateException {
        try {
            temp = File.createTempFile("sgoUpd", Long.toString(System.nanoTime()));
        } catch (Exception e) {
            throw new updateException(updateException.TEMP_DIR_ERROR);
        }
        System.out.println(temp);
        if (!(temp.delete())) {
            throw new updateException(updateException.TEMP_DIR_ERROR);
        }

        if (!(temp.mkdir())) {
            throw new updateException(updateException.TEMP_DIR_ERROR);
        }
        return parseUpdate.parse(commands, temp);
    }

    public void cleanUp() {
        try {
            FileUtils.deleteDirectory(temp);
        } catch (Exception e) {
        }
    }
}
