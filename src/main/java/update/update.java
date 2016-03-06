/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import sun.misc.BASE64Encoder;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 05.02.2016.
 * This class is used to update the application and the Decred binaries
 */

public class update {
    public Updating updateWindow;
    private updateInfo updateToDo;
    private BigInteger modulus;
    private BigInteger pubExp;

    public update(BigInteger modulus, BigInteger pubExp, String infoUrl) throws updateException {
        this.pubExp = pubExp;
        this.modulus = modulus;
        this.updateToDo = new updateInfo(infoUrl);
    }

    /**
     * Sign a message with a given key file
     * @param message The message to sign
     * @param keyfile The private key files directory
     * @return THe new signature
     */
    public static String signString(String message, String keyfile) {
        try {
            File f = new File(keyfile);
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);
            dis.close();

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKey key = (RSAPrivateKey) kf.generatePrivate(spec);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(key);
            signature.update(message.getBytes());
            byte[] sigBytes = signature.sign();
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(sigBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean updateAvailable(long currentVersion, long currentSoftware){
        return updateToDo.updateAvailable(currentVersion, currentSoftware);
    }

    public void run(long currentSoftware) throws updateException {
        updateCommands commands = updateToDo.getCommands(currentSoftware);
        if (!commands.checkSignature(modulus, pubExp)) throw new updateException(updateException.INVALID_SIGNATURE);
        updateItem[] todo = commands.parse();
        updateWindow = new Updating(); //Show the update progress
        for (int i = 0; i < todo.length; i++) { //now parse and execute each command one after the other
            updateWindow.setProgress(((float) (i + 1)) / ((float) todo.length));
            if (!todo[i].execute()) {
                System.out.println("Command not executed");
                updateWindow.dispose();
                return;
            }
            if (!todo[i].check()) {
                System.out.println("Command invalid");
                updateWindow.dispose();
                return;
            }
        }
        updateWindow.dispose();
        commands.cleanUp();
    }
}
