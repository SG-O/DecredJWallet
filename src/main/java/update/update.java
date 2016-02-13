/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 05.02.2016.
 * This class is used to update the application and the Decred binaries
 */

public class update {
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguimainupdate";
    public static final String DCRDWIN64_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsw64";
    public static final String DCRDWIN32_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsw32";
    public static Updating updateWindow;

    //check for updates
    public static boolean checkUpdates() {
        return softwareInfo.version != getUpdatesInfo(UPDATE_URL).optLong("Version", 0);
    }

    //check for updates for the Decred binaries
    //TODO:This has to be rewritten entirely as it is ugly.
    public static long checkTools(long versionNow){
        if (!SystemUtils.IS_OS_WINDOWS)return -1;
        boolean is64bit = false;
        if (System.getProperty("os.name").contains("Windows")) {
            is64bit = (System.getenv("ProgramFiles(x86)") != null);
        } else {
            is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
        }
        if (is64bit){
            long now = getUpdatesInfo(DCRDWIN64_URL).optLong("Version", 0);
            if(versionNow == now) return -1;
            return now;
        } else {
            long now = getUpdatesInfo(DCRDWIN32_URL).optLong("Version", 0);
            if(versionNow == now) return -1;
            return now;
        }

    }

    //Update the application
    public static boolean doUpdate(){
        boolean temp = doUpdate(UPDATE_URL);
        System.exit(0);
        return temp;
    }

    //Download or update the Decred binaries
    public static boolean getTools(){
        if (!SystemUtils.IS_OS_WINDOWS)return false;
        boolean is64bit = false;
        if (System.getProperty("os.name").contains("Windows")) {
            is64bit = (System.getenv("ProgramFiles(x86)") != null);
        } else {
            is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
        }
        if (is64bit){
            return doUpdate(DCRDWIN64_URL);
        } else {
            return doUpdate(DCRDWIN32_URL);
        }
    }

    /**
     * Execute an update with the commands found at the given URL
     * @param url
     * @return
     */
    public static boolean doUpdate(String url) {
        JSONObject info = getUpdatesInfo(url);
        if (!info.has("Server")) { //Check if update information has information about the location of the update commands
            System.out.println("No Update Information available");
            return false;
        }
        String infoServer = info.getString("Server");
        System.out.println(infoServer);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(infoServer).openStream()));
            JSONObject commandFile = new JSONObject(in.readLine()); //Now read and parse the file containing the commands that are required to execute the update.
            if (!commandFile.has("Commands")) {
                System.out.println("No commands available");
                return false;
            }
            JSONArray commands = commandFile.optJSONArray("Commands");
            String signature = commandFile.optString("Signature", "");
            if (!checkSignature(commands.toString(), signature)) { //Now check the signature to verify that the update has not been tempered with
                System.out.println("Invalid update signature\n"+commands.toString()+ "\n"+ signature);
                return false;
            }
            File temp = File.createTempFile("dcrd", Long.toString(System.nanoTime())); //create a temporary directory where the update files can be downloaded to
            System.out.println(temp);
            if(!(temp.delete()))
            {
                System.out.println("Could not create temp dir");
                return false;
            }

            if(!(temp.mkdir()))
            {
                System.out.println("Could not create temp dir");
                return false;
            }
            updateWindow = new Updating(); //Show the update progress
            updateItem[] todo = parseUpdate.parse(commands, temp);
            for (int i = 0; i < todo.length; i++){ //now parse and execute each command one after the other
                updateWindow.setProgress(((float)(i+1))/((float)todo.length));
                if (todo[i].execute() == false) {
                    System.out.println("Command not executed");
                    updateWindow.dispose();
                    return false;
                }
                if (todo[i].check() == false) {
                    System.out.println("Command invalid");
                    updateWindow.dispose();
                    return false;
                }
            }
            temp.delete();
            updateWindow.dispose();

        } catch (Exception e) {
            System.out.println("UpdateExeption:" + e);
            return false;
        }
        return true;
    }

    /**
     * Read the update information file at the given URL
     * @param url The URL the info file is located at.
     * @return The parsed JSON Object
     */
    public static JSONObject getUpdatesInfo(String url) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            JSONObject test = new JSONObject(in.readLine());
            in.close();
            return test;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * Check the message for authenticity
     * @param message The message to check
     * @param sig It's signature
     * @return True if message is OK.
     */
    public static boolean checkSignature(String message, String sig) {
        try {
            BigInteger modulus = new BigInteger("C72E404930B0AD90DB785AF1F640A653075A6525EA6067D1949F80D722A1E9E9EF5F71EFFD0250524378ED25D7585E47502465B623961554A706AB755F7B82F6ED611F1190D7BD310DC04A797AA1FE327E5FE12E316DF0D217A2DB421441DF96FCA1F09E968B5F9FEB7E26AD94D40D9818882A209FCAB5D06B0E42996B69C718C8286908C73A32F229ED36B99BA2070FD3547F87B47562329067DFCABBF268622D8D020F49660468DC337758C0AD92D8D275854234E2E8817B3118F5AF45935825CCFC3F5057C940B2932A02E0637945D28314D66DC776F73121DF862610AA65056FDD6E6C37E5F0D75A22AD8942B4476CEE22A6B65FF94B48E325719C5DC1D9", 16); //This is the hard coded public key. DO NOT CHANGE unless you know what you are doing
            BigInteger pubExp = new BigInteger("010001", 16);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(key);
            signature.update(message.getBytes());
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] sigBytes = decoder.decodeBuffer(sig);
            return signature.verify(sigBytes);
        } catch (Exception e) {
            return false;
        }
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
            System.out.println(e);
            return null;
        }
    }
}
