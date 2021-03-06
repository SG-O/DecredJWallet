/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 04.03.2016.
 */

import internal.base58;
import internal.hashWrapper;
import internal.hex;

import java.util.Arrays;
import java.util.HashMap;

public class address {
    private byte[] publicKeyHash;
    private byte[] shortHash;
    private byte[] networkBytes;

    public address(byte[] publicKeyHash, byte[] networkBytes) {
        this.publicKeyHash = publicKeyHash;
        this.networkBytes = networkBytes;
        this.shortHash = hashWrapper.shortDoubleHashSha256(getKeyWithNet());
    }

    public address(String address, int netIdentifierLength) {
        if (address == null) return;
        if (address.equals("")) return;
        address = address.trim();
        if (!(address.length() > (netIdentifierLength + 4))) return;
        byte[] addressHex = base58.fromBase58(address);
        HashMap<Integer, byte[]> seperated = hex.hexSeparate(addressHex, netIdentifierLength);
        if (seperated != null) {
            networkBytes = seperated.getOrDefault(0, new byte[0]);
            seperated = hex.hexSeparate(seperated.getOrDefault(1, new byte[0]), (addressHex.length - 4) - netIdentifierLength);
            if (seperated != null) {
                this.publicKeyHash = seperated.getOrDefault(0, new byte[0]);
                this.shortHash = seperated.getOrDefault(1, new byte[0]);
            }
        }
    }

    public boolean isEmpty(){
        if ((publicKeyHash == null) || (shortHash == null) || (networkBytes == null)) return true;
        return (publicKeyHash.length == 0) || (shortHash.length == 0) || (networkBytes.length == 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        address address = (address) o;

        if (!Arrays.equals(publicKeyHash, address.publicKeyHash)) return false;
        if (!Arrays.equals(shortHash, address.shortHash)) return false;
        return Arrays.equals(networkBytes, address.networkBytes);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(publicKeyHash);
        result = 31 * result + Arrays.hashCode(shortHash);
        result = 31 * result + Arrays.hashCode(networkBytes);
        return result;
    }

    public String getHashString(){
        return hex.bytesToHex(calculateShortHash());
    }

    public boolean check() {
        return Arrays.equals(shortHash, calculateShortHash());
    }

    public byte[] getPublicKeyHash() {
        return publicKeyHash;
    }

    public byte[] getShortHash() {
        return shortHash;
    }

    public byte[] getNetworkBytes() {
        return networkBytes;
    }

    public byte[] calculateShortHash() {
        return hashWrapper.shortDoubleHashSha256(getKeyWithNet());
    }

    @Override
    public String toString() {
        return base58.toBase58(toHex());
    }

    public String toHexString() {
        return hex.bytesToHex(toHex());
    }

    public byte[] getKeyWithNet() {
        return hex.hexAppend(networkBytes, publicKeyHash);
    }

    public byte[] toHex() {
        return hex.hexAppend(getKeyWithNet(), shortHash);
    }
}
