/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.prefs.Preferences;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 01.02.2016.
 * This class stores all the settings, saves them and loads them
 */
public class settings {
    private static decredBackend backend;
    private final Preferences pref = Preferences.userRoot();
    private String RPCUser;
    private String RPCPass;
    private String RPCAddr;
    private boolean testnet;
    private boolean RPCtls = true;
    private boolean firstrun;
    private boolean encryption = false;
    private int transactionsToLoade = 100;
    private boolean doAutoUpdate = true;
    private boolean fairDonation = false;
    private boolean fairDonationCustomAmount = false;
    private String customUpdateUrl = "";
    private String customBinariesUrl = "";
    private Coin fairDonationCustom = new Coin();

    private Cipher cipher;

    public settings(String RPCUser, String RPCPass, String RPCAddr, boolean testnet, boolean tls, boolean encryption, int transactionsToLoade) {
        this.RPCUser = RPCUser;
        this.RPCPass = RPCPass;
        this.RPCAddr = RPCAddr;
        this.testnet = testnet;
        this.RPCtls = tls;
        this.encryption = encryption;
        this.firstrun = pref.getBoolean("DCRDfirstrun", true);
        this.transactionsToLoade = transactionsToLoade;
    }

    public settings(String RPCUser, String RPCPass, String RPCAddr, boolean testnet) {
        this.RPCUser = RPCUser;
        this.RPCPass = RPCPass;
        this.RPCAddr = RPCAddr;
        this.testnet = testnet;
        this.encryption = false;
        this.firstrun = pref.getBoolean("DCRDfirstrun", true);
    }

    public settings(String RPCUser, String RPCPass, boolean testnet) {
        this.RPCUser = RPCUser;
        this.RPCPass = RPCPass;
        this.testnet = testnet;
        this.RPCAddr = "127.0.0.1";
        this.encryption = false;
        this.firstrun = pref.getBoolean("DCRDfirstrun", true);
    }

    /**
     * Load all settings
     */
    public settings() {
        this.RPCUser = pref.get("RPCUser", "local");
        this.RPCPass = pref.get("RPCPass", "none");
        this.RPCAddr = pref.get("RPCAddr", "127.0.0.1");
        this.testnet = pref.getBoolean("DCRDtestnet", false);
        this.RPCtls = pref.getBoolean("RPCtls", true);
        this.transactionsToLoade = pref.getInt("DCRDtransactions", 100);
        this.encryption = pref.getBoolean("DCRDencrypted", false);
        this.firstrun = pref.getBoolean("DCRDfirstrun", true);
        this.doAutoUpdate = pref.getBoolean("DCRDupdate", true);
        this.fairDonation = pref.getBoolean("DCRDfairDonation", false);
        this.fairDonationCustomAmount = pref.getBoolean("DCRDfairDonationCustomAmount", false);
        this.customUpdateUrl = pref.get("DCRDcustomUpdateUrl", "");
        this.customBinariesUrl = pref.get("DCRDcustomBinariesUrl", "");
        try {
            this.fairDonationCustom = new Coin(pref.getDouble("DCRDfairDonationCustom", 0));
        } catch (Exception e) {
        }

        if (encryption) {
            DecryptForm decryptf = new DecryptForm();
            while (decryptf.key == null){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            this.RPCPass = decrypt(this.getRPCPass(), decryptf.key);
        }

    }

    public static decredBackend getBackend() {
        return backend;
    }

    /**
     * Connect to the backend with the current settings
     */
    public void connect() {
        backend = new decredBackend(this);
    }

    public String getRPCUser() {
        return RPCUser;
    }

    public void setRPCUser(String RPCUser) {
        this.RPCUser = RPCUser.trim();
    }

    public String getRPCPass() {
        return RPCPass;
    }

    public void setRPCPass(String RPCPass) {
        this.RPCPass = RPCPass;
    }

    public String getRPCAddr() {
        return RPCAddr;
    }

    public void setRPCAddr(String RPCAddr) {
        this.RPCAddr = RPCAddr.trim();
    }

    public boolean isTestnet() {
        return testnet;
    }

    public void setTestnet(boolean testnet) {
        this.testnet = testnet;
    }

    public boolean isRPCtls() {
        return RPCtls;
    }

    public void setRPCtls(boolean RPCtls) {
        this.RPCtls = RPCtls;
    }

    public boolean isFirstrun() {
        return firstrun;
    }

    public boolean isEncryption() {
        return encryption;
    }

    public int getTransactionsToLoad() {
        if (transactionsToLoade < 1) transactionsToLoade = 1;
        return transactionsToLoade;
    }

    public void setTransactionsToLoade(int transactionsToLoade) {
        this.transactionsToLoade = transactionsToLoade;
    }

    public boolean isDoAutoUpdate() {
        return doAutoUpdate;
    }

    public void setDoAutoUpdate(boolean doAutoUpdate) {
        this.doAutoUpdate = doAutoUpdate;
    }

    public boolean isFairDonation() {
        return fairDonation;
    }

    public void setFairDonation(boolean fairDonation) {
        this.fairDonation = fairDonation;
    }

    public boolean isFairDonationCustomAmount() {
        return fairDonationCustomAmount;
    }

    public void setFairDonationCustomAmount(boolean fairDonationCustomAmount) {
        this.fairDonationCustomAmount = fairDonationCustomAmount;
    }

    public Coin getFairDonationCustom() {
        return fairDonationCustom;
    }

    public void setFairDonationCustom(Coin fairDonationCustom) {
        this.fairDonationCustom = fairDonationCustom;
    }

    public String getCustomUpdateUrl() {
        return customUpdateUrl;
    }

    public boolean isCustomUpdateUrl() {
        if (customUpdateUrl == null) return false;
        return !customUpdateUrl.isEmpty();
    }

    public void setCustomUpdateUrl(String customUpdateUrl) {
        this.customUpdateUrl = customUpdateUrl.trim();
    }

    public String getCustomBinariesUrl() {
        return customBinariesUrl;
    }

    public boolean isCustomBinariesUrl() {
        if (customBinariesUrl == null) return false;
        return !customBinariesUrl.isEmpty();
    }

    public void setCustomBinariesUrl(String customBinariesUrl) {
        this.customBinariesUrl = customBinariesUrl;
    }

    public address getDonationAddress() {
        if (!testnet) return new address("DshRV5v8XEVpozeEEaZffvtzrfJLqBnYRnX", 2);
        return new address("TskQPYijAN2MCmmeymqzpWPQXjbdty8YzJa", 2);
    }

    /**
     * Encrypt the given string with a key using AES
     * @param plain The plaintext to encrypt
     * @param key The key to use
     * @return The encrypted text
     */
    public String encrypt(String plain, String key) {
        try {
            cipher = Cipher.getInstance("AES");
            byte[] plainTextByte = plain.getBytes();

            cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
            byte[] encryptedByte = cipher.doFinal(plainTextByte);
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(encryptedByte);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypt the given string with a key using AES
     * @param crypt The encrypted text to decrypt
     * @param key The key to use
     * @return The plaintext
     */
    public String decrypt(String crypt, String key) {
        try {
            cipher = Cipher.getInstance("AES");
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encryptedTextByte = decoder.decode(crypt);
            cipher.init(Cipher.DECRYPT_MODE, getKey(key));
            byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
            return new String(decryptedByte);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generate a secret key from a string by hashing it
     * @param key The key that should be used
     * @return The Secret key
     * @throws Exception If there has been an error return an exception
     */
    private SecretKeySpec getKey(String key) throws Exception {
        byte[] byteKey = (key).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byteKey = sha.digest(byteKey);
        byteKey = Arrays.copyOf(byteKey, 16); // use only first 128 bit

        return new SecretKeySpec(byteKey, "AES");

    }

    /**
     * Save the current configuration without encryption
     */
    public void savefullConfig() {
        savefullConfig("");
    }

    /**
     * Save the current configuration and encrypt the password
     * @param key They the password should be encrypted with. If this is empty or null no encryption is used.
     */
    public void savefullConfig(String key) {
        pref.put("RPCUser", this.RPCUser);
        this.encryption = false;
        if (key == null) {
            pref.put("RPCPass", this.RPCPass);
        }else if (key.equals("")) {
            pref.put("RPCPass", this.RPCPass);
        } else {
            String encryptedRPC = encrypt(this.RPCPass, key);
            if (encryptedRPC == null){
                pref.put("RPCPass", this.RPCPass);
            }else {
                pref.put("RPCPass", encryptedRPC);
                this.encryption = true;
            }
        }
        pref.putBoolean("DCRDencrypted", this.encryption);
        saveConfig();
    }

    public void saveConfig() {
        pref.put("RPCAddr", this.RPCAddr);
        pref.putBoolean("RPCtls", this.RPCtls);
        pref.putBoolean("DCRDtestnet", this.testnet);
        pref.putBoolean("DCRDfirstrun", false);
        pref.putInt("DCRDtransactions", this.transactionsToLoade);
        pref.putBoolean("DCRDupdate", this.doAutoUpdate);
        pref.putBoolean("DCRDfairDonation", this.fairDonation);
        pref.putBoolean("DCRDfairDonationCustomAmount", this.fairDonationCustomAmount);
        pref.putDouble("DCRDfairDonationCustom", this.fairDonationCustom.getAmount());
        pref.put("DCRDcustomUpdateUrl", this.customUpdateUrl);
        pref.put("DCRDcustomBinariesUrl", this.customBinariesUrl);
    }

    @Override
    public String toString() {
        return "settings{" +
                "RPCUser='" + RPCUser + '\'' +
                ", RPCAddr='" + RPCAddr + '\'' +
                ", RPCtls=" + RPCtls +
                ", testnet=" + testnet +
                '}';
    }
}
