/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 31.01.2016.
 */
public class status extends Throwable{
    public static final int SUCCESS = 0;
    public static final int GENERICERROR = 1;
    public static final int WRONGKEY = 2;
    public static final int CHECKSUMMISMATCH = 3;
    public static final int LOCKED = 4;
    public static final int FUNDS = 5;
    public static final int DOUBLESPEND = 6;

    private int status;

    public status(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        switch (status){
            case SUCCESS: return "Success";
            case GENERICERROR: return "Error";
            case WRONGKEY: return "Wrong key";
            case CHECKSUMMISMATCH: return "Wrong address: Checksum mismatch";
            case LOCKED: return "Wallet is locked";
            case FUNDS: return "Insufficient funds";
            case DOUBLESPEND: return "Tried to double spend";
            default: return "unknown";
        }
    }
}
