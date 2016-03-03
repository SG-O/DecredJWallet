/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 31.01.2016.
 * This class is used to communicate with the backend and parse its results.
 */
public class decredBackend {
    public static long walletPort = 9110;
    public static long decredPort = 9109;
    public static long testWalletPort = 19110;
    public static long testDecredPort = 19109;
    public static boolean USE_WALLET = true;
    public static boolean USE_DECRED = false;

    private RPC connection;
    private String address;
    private boolean testnet;

    private Coin txFee = new Coin();
    private settings set;

    /**
     * Connet to the backend with the given Settings
     * @param set the settings to load from.
     */
    public decredBackend(settings set) {
        connection = new RPC(set.getRPCUser(), set.getRPCPass(), set.isRPCtls());
        this.address = set.getRPCAddr();
        this.testnet = set.isTestnet();
        this.set = set;
    }

    /**
     * Check if we are connected to the backend
     * @return True if we are
     */
    public boolean checkConnection() {
        if (connection == null) return false;
        if (!connection.goodConnection(address, getPort(USE_DECRED))) return false;
        return connection.goodConnection(address, getPort(USE_WALLET));
    }

    /**
     * Get the balance of the current user
     * @return The balanca as a fixedpoint long integer
     */
    public Coin getBalance() throws status {
        try {
            JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.GETBALANCE));
            comunicationStrings.increaseIndex();
            if (!temp.has("result")) throw new status(status.GENERICERROR);
            try {
                return new Coin(temp.getDouble("result"));
            } catch (Exception e1) {
                throw new status(status.GENERICERROR);
            }
        } catch (Exception e) {
            throw new status(status.GENERICERROR);
        }
    }

    /**
     * Get the unconfirmed balance of the current user
     * @return The balanca as a fixedpoint long integer
     */
    public Coin getUnconfirmedBalance() throws status {
        try {
            JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.GETUNCONFIRMEDBALANCE));
            comunicationStrings.increaseIndex();
            if (!temp.has("result")) throw new status(status.GENERICERROR);
            try {
                return new Coin(temp.getDouble("result"));
            } catch (Exception e1) {
                throw new status(status.GENERICERROR);
            }
        } catch (Exception e) {
            throw new status(status.GENERICERROR);
        }
    }

    /**
     * Get the unconfirmed balance of the current user
     *
     * @return The balanca as a fixedpoint long integer
     */
    public long getBlockCount() throws status {
        try {
            JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_DECRED), comunicationStrings.GETBLOCKCOUNT));
            comunicationStrings.increaseIndex();
            if (!temp.has("result")) throw new status(status.GENERICERROR);
            try {
                return temp.optLong("result", 0l);
            } catch (Exception e1) {
                throw new status(status.GENERICERROR);
            }
        } catch (Exception e) {
            throw new status(status.GENERICERROR);
        }
    }

    public long getWalletBlockCount() throws status {
        try {
            JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.GETBLOCKCOUNT));
            comunicationStrings.increaseIndex();
            if (!temp.has("result")) throw new status(status.GENERICERROR);
            try {
                return temp.optLong("result", 0l);
            } catch (Exception e1) {
                throw new status(status.GENERICERROR);
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new status(status.GENERICERROR);
        }
    }

    /**
     * List a number of transactions
     * @param n The maximum number of transactions to load
     * @return An array of transactions
     */
    public transaction[] listTransactions(int n) throws status {
        try {
            JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.LISTTRANSACTIONS(n)));
            comunicationStrings.increaseIndex();
            if (!temp.has("result")) throw new status(status.GENERICERROR);
            JSONArray transactions = temp.optJSONArray("result");
            transaction[] tempArray = new transaction[transactions.length()];
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject currentTransaction = transactions.getJSONObject(i);
                tempArray[i] = new transaction(currentTransaction.optString("txid"), currentTransaction.optString("blockhash"), currentTransaction.optString("address", ""), new Coin(currentTransaction.optDouble("amount")), new Coin(currentTransaction.optDouble("fee")), currentTransaction.optInt("confirmations"), currentTransaction.optString("category"), currentTransaction.optLong("time"));
            }
            return tempArray;
        } catch (Exception e){
            throw new status(status.GENERICERROR);
        }
    }

    /**
     * Try to unlock the wallet forever with a given key
     * @param key The key the wallet is locked with
     * @return The status
     */
    public status unlockWallet(String key) {
        return unlockWallet(key, 0);
    }

    /**
     * Unlock the wallet for a specific time
     * @param key The key the wallet is locked with
     * @param time The time in seconds the wallet should stay unlocked
     * @return The status
     */
    public status unlockWallet(String key, int time) {
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.UNLOCKWALLET(key, time)));
        comunicationStrings.increaseIndex();
        System.out.println(temp);
        if (!temp.has("error")) return new status(status.GENERICERROR);
        if (temp.isNull("error")) return new status(status.SUCCESS);
        if (temp.optJSONObject("error").optLong("code") == -14) return new status(status.WRONGKEY);
        return new status(status.GENERICERROR);
    }

    /**
     * Lock the wallet
     */
    public void lockWallet() {
        connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.LOCKWALLET);
        comunicationStrings.increaseIndex();
    }

    /**
     * Sond some funds to an address
     * @param toAddress The address the funds should be sent to
     * @param amount The amount of founds that should be transferd in the form of a fixed point long integer
     * @return The transaction ID
     * @throws status If there was an error throw the status
     */
    public String sendToAddress(String toAddress, Coin amount) throws status {
        if (set.isFairDonation()) {
            HashMap<String, Coin> temp = new HashMap<String, Coin>();
            temp.put(toAddress, amount);
            return sendMany(temp);
        }
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.SENDTO(toAddress, amount)));
        comunicationStrings.increaseIndex();
        if (!temp.has("result")) throw new status(status.GENERICERROR);
        System.out.println(temp);
        if (temp.has("error")) {
            if (!temp.isNull("error")) {
                if (temp.optJSONObject("error").optLong("code") == -32603) {
                    if (temp.optJSONObject("error").optString("message").startsWith("insufficient funds"))
                        throw new status(status.FUNDS);
                    if (temp.optJSONObject("error").optString("message").startsWith("cannot decode address"))
                        throw new status(status.CHECKSUMMISMATCH);
                    if (temp.optJSONObject("error").optString("message").startsWith("-22: TX rejected"))
                        throw new status(status.DOUBLESPEND);
                    throw new status(status.GENERICERROR);
                }
                if (temp.optJSONObject("error").optLong("code") == -13) throw new status(status.LOCKED);
                throw new status(status.GENERICERROR);
            }
        }
        return temp.getString("result");
    }

    public String sendMany(HashMap<String, Coin> addresses) throws status {
        return sendMany("default", addresses);
    }

    public String sendMany(String account, HashMap<String, Coin> addresses) throws status {
        if (set.isFairDonation()) {
            Coin donation;
            if (set.isFairDonationCustomAmount()) {
                donation = set.getFairDonationCustom();
            } else {
                donation = txFee;
            }
            addresses.put(set.getDonationAddress(), donation);
        }
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.SENDMANY(account, addresses)));
        comunicationStrings.increaseIndex();
        if (!temp.has("result")) throw new status(status.GENERICERROR);
        System.out.println(temp);
        if (temp.has("error")) {
            if (!temp.isNull("error")) {
                if (temp.optJSONObject("error").optLong("code") == -32603) {
                    if (temp.optJSONObject("error").optString("message").startsWith("insufficient funds"))
                        throw new status(status.FUNDS);
                    if (temp.optJSONObject("error").optString("message").startsWith("cannot decode address"))
                        throw new status(status.CHECKSUMMISMATCH);
                    if (temp.optJSONObject("error").optString("message").startsWith("-22: TX rejected"))
                        throw new status(status.DOUBLESPEND);
                    throw new status(status.GENERICERROR);
                }
                if (temp.optJSONObject("error").optLong("code") == -13) throw new status(status.LOCKED);
                throw new status(status.GENERICERROR);
            }
        }
        return temp.getString("result");
    }

    /**
     * List all addresses that a account has
     * @param acont The account to check
     * @return An array of addresses
     * @throws status If there was an error throw the status
     */
    public String[] getAddresses(String acont) throws status{
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.GETADDRESSES(acont)));
        comunicationStrings.increaseIndex();
        if (!temp.has("result")) throw new status(status.GENERICERROR);
        System.out.println(temp);
        if (temp.has("error")) {
            if (!temp.isNull("error")) {
                throw new status(status.GENERICERROR);
            }
        }
        JSONArray array = temp.optJSONArray("result");
        if ((array) == null){
            String content = temp.optString("result", "");
            if (content.equals("")) throw new status(status.NOADDRESSES);
            String[] out = {content};
            return out;
        }
        String[] out = new String[array.length()];
        for (int i = 0; i < array.length(); i++){
            out[i] = array.getString(i);
        }
        return out;
    }

    /**
     * Generate a new address
     * @return The newly generated address
     * @throws status If there was an error throw the status
     */
    public String getNewAddress() throws status{
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.GETNEWADDRESS));
        comunicationStrings.increaseIndex();
        if (!temp.has("result")) throw new status(status.GENERICERROR);
        System.out.println(temp);
        if (temp.has("error")) {
            if (!temp.isNull("error")) {
                throw new status(status.GENERICERROR);
            }
        }
        String content = temp.optString("result", "");
        if (content.equals("")) throw new status(status.GENERICERROR);
        return content;
    }

    /**
     * Get the relay fee tat is currently used
     * @return The relay fee as a fixed point long integer
     * @throws status If there was an error throw the status
     */
    public Coin getRelayFee() throws status {
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.GETINFO));
        comunicationStrings.increaseIndex();
        System.out.println(temp);
        if (!temp.has("result")) throw new status(status.GENERICERROR);
        if (temp.has("error")) {
            if (!temp.isNull("error")) {

            }
        }
        JSONObject info = temp.optJSONObject("result");
        if(info == null) throw new status(status.GENERICERROR);
        try {
            return new Coin(info.optDouble("relayfee", 0d));
        } catch (Exception e) {
            throw new status(status.GENERICERROR);
        }
    }

    /**
     * Set the transaction fee to a custom value
     * @param amount The new transaction fee in the form of a fixed point long integer
     * @throws status If there was an error throw the status
     */
    public void setTxFee(Coin amount) throws status {
        this.txFee = amount;
        JSONObject temp = new JSONObject(connection.getRequestAnswer(address, getPort(USE_WALLET), comunicationStrings.SETTXFEE(amount)));
        comunicationStrings.increaseIndex();
        System.out.println(temp);
        if (!temp.has("result")) throw new status(status.GENERICERROR);
        if (temp.has("error")) {
            if (!temp.isNull("error")) {
                throw new status(status.GENERICERROR);
            }
        }
        boolean result = temp.optBoolean("result", false);
        if(result == false) throw new status(status.GENERICERROR);
    }

    /**
     * Return the correct port
     * @param wallet If true get the port for the wallet
     * @return Return the correct port.
     */
    public long getPort(boolean wallet) {
        if (wallet && this.testnet) {
            return testWalletPort;
        }
        if (this.testnet) {
            return testDecredPort;
        }
        if (wallet) {
            return walletPort;
        }
        return decredPort;
    }
}
