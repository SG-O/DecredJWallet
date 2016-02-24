/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 31.01.2016.
 * These are the strings used to communicate with the backend.
 */
public class comunicationStrings {
    private static int index = 0;

    public static String GETBALANCE = "{\"method\":\"getbalance\",\"params\":[],\"id\":"+index+"}";
    public static String GETUNCONFIRMEDBALANCE = "{\"method\":\"getunconfirmedbalance\",\"params\":[],\"id\":" + index + "}";
    public static String GETBLOCKCOUNT = "{\"method\":\"getblockcount\",\"params\":[],\"id\":" + index + "}";
    public static String LOCKWALLET = "{\"method\":\"walletlock\",\"params\":[],\"id\":"+index+"}";
    public static String GETNEWADDRESS = "{\"method\":\"getnewaddress\",\"params\":[],\"id\":"+index+"}";
    public static String GETINFO = "{\"method\":\"getinfo\",\"params\":[],\"id\":"+index+"}";

    public static String LISTTRANSACTIONS(int n) {
        return"{\"method\":\"listtransactions\",\"params\":[\"*\", "+ n +", 0],\"id\":"+index+"}";
    }

    public static String UNLOCKWALLET(String key, long time){
        return "{\"method\":\"walletpassphrase\",\"params\":[\"" + key + "\"," + time + "],\"id\":"+index+"}";
    }

    public static String SENDTO(String address, Coin amount) {
        return "{\"method\":\"sendtoaddress\",\"params\":[\"" + address + "\"," + amount + "],\"id\":" + index + "}";
    }

    public static String GETADDRESSES(String account){
        return "{\"method\":\"getaddressesbyaccount\",\"params\":[\"" + account + "\"],\"id\":"+index+"}";
    }


    public static String SETTXFEE(Coin amount) {
        return "{\"method\":\"settxfee\",\"params\":[" + amount + "],\"id\":" + index + "}";
    }
    public static void increaseIndex(){
        index++;
        if (index>255) index = 0;
    }
}
