/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import internal.hex;

import java.util.Arrays;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 09.03.2016.
 */
public class decredConstants {
    public static final decredConstants mainNet = new decredConstants(
            hex.hexToBytes("1386"),
            hex.hexToBytes("073f"),
            hex.hexToBytes("071f"),
            hex.hexToBytes("0701"),
            hex.hexToBytes("071a"),
            hex.hexToBytes("22de"),
            hex.hexToBytes("02fda4e8"),
            hex.hexToBytes("02fda926"),
            9110,
            9109);

    public static final decredConstants testNet = new decredConstants(
            hex.hexToBytes("28f7"),
            hex.hexToBytes("0f21"),
            hex.hexToBytes("0f01"),
            hex.hexToBytes("0ee3"),
            hex.hexToBytes("0efc"),
            hex.hexToBytes("230e"),
            hex.hexToBytes("04358397"),
            hex.hexToBytes("043587d1"),
            19110,
            19109);


    private byte[] pubKeyAddrID;
    private byte[] pubKeyHashAddrID;
    private byte[] pKHEdwardsAddrID;
    private byte[] pKHSchnorrAddrID;
    private byte[] scriptHashAddrID;
    private byte[] privateKeyID;

    private byte[] HDPrivateKeyID;
    private byte[] HDPublicKeyID;

    private long walletPort;
    private long decredPort;

    public decredConstants(byte[] pubKeyAddrID, byte[] pubKeyHashAddrID, byte[] pKHEdwardsAddrID, byte[] pKHSchnorrAddrID, byte[] scriptHashAddrID, byte[] privateKeyID, byte[] HDPrivateKeyID, byte[] HDPublicKeyID, long walletPort, long decredPort) {
        this.pubKeyAddrID = pubKeyAddrID;
        this.pubKeyHashAddrID = pubKeyHashAddrID;
        this.pKHEdwardsAddrID = pKHEdwardsAddrID;
        this.pKHSchnorrAddrID = pKHSchnorrAddrID;
        this.scriptHashAddrID = scriptHashAddrID;
        this.privateKeyID = privateKeyID;
        this.HDPrivateKeyID = HDPrivateKeyID;
        this.HDPublicKeyID = HDPublicKeyID;
        this.walletPort = walletPort;
        this.decredPort = decredPort;
    }

    public static decredConstants getNetConstants(settings set) {
        if (set.isTestnet()) return testNet;
        return mainNet;
    }

    public byte[] getPubKeyAddrID() {
        return pubKeyAddrID;
    }

    public byte[] getPubKeyHashAddrID() {
        return pubKeyHashAddrID;
    }

    public byte[] getpKHEdwardsAddrID() {
        return pKHEdwardsAddrID;
    }

    public byte[] getpKHSchnorrAddrID() {
        return pKHSchnorrAddrID;
    }

    public byte[] getScriptHashAddrID() {
        return scriptHashAddrID;
    }

    public byte[] getPrivateKeyID() {
        return privateKeyID;
    }

    public byte[] getHDPrivateKeyID() {
        return HDPrivateKeyID;
    }

    public byte[] getHDPublicKeyID() {
        return HDPublicKeyID;
    }

    public long getWalletPort() {
        return walletPort;
    }

    public long getDecredPort() {
        return decredPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        decredConstants that = (decredConstants) o;

        if (walletPort != that.walletPort) return false;
        if (decredPort != that.decredPort) return false;
        if (!Arrays.equals(pubKeyAddrID, that.pubKeyAddrID)) return false;
        if (!Arrays.equals(pubKeyHashAddrID, that.pubKeyHashAddrID)) return false;
        if (!Arrays.equals(pKHEdwardsAddrID, that.pKHEdwardsAddrID)) return false;
        if (!Arrays.equals(pKHSchnorrAddrID, that.pKHSchnorrAddrID)) return false;
        if (!Arrays.equals(scriptHashAddrID, that.scriptHashAddrID)) return false;
        if (!Arrays.equals(privateKeyID, that.privateKeyID)) return false;
        if (!Arrays.equals(HDPrivateKeyID, that.HDPrivateKeyID)) return false;
        return Arrays.equals(HDPublicKeyID, that.HDPublicKeyID);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(pubKeyAddrID);
        result = 31 * result + Arrays.hashCode(pubKeyHashAddrID);
        result = 31 * result + Arrays.hashCode(pKHEdwardsAddrID);
        result = 31 * result + Arrays.hashCode(pKHSchnorrAddrID);
        result = 31 * result + Arrays.hashCode(scriptHashAddrID);
        result = 31 * result + Arrays.hashCode(privateKeyID);
        result = 31 * result + Arrays.hashCode(HDPrivateKeyID);
        result = 31 * result + Arrays.hashCode(HDPublicKeyID);
        result = 31 * result + (int) (walletPort ^ (walletPort >>> 32));
        result = 31 * result + (int) (decredPort ^ (decredPort >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "decredConstants{" +
                "pubKeyAddrID=" + Arrays.toString(pubKeyAddrID) +
                ", pubKeyHashAddrID=" + Arrays.toString(pubKeyHashAddrID) +
                ", pKHEdwardsAddrID=" + Arrays.toString(pKHEdwardsAddrID) +
                ", pKHSchnorrAddrID=" + Arrays.toString(pKHSchnorrAddrID) +
                ", scriptHashAddrID=" + Arrays.toString(scriptHashAddrID) +
                ", privateKeyID=" + Arrays.toString(privateKeyID) +
                ", HDPrivateKeyID=" + Arrays.toString(HDPrivateKeyID) +
                ", HDPublicKeyID=" + Arrays.toString(HDPublicKeyID) +
                ", walletPort=" + walletPort +
                ", decredPort=" + decredPort +
                '}';
    }
}
