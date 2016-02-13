/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 31.01.2016.
 */
public class fixedPoint {
    public static double longToCoin(long value){
        return  ((double) value / 100000000L);
    }
    public static long coinToLong(double value) {
        return (long) (value * 100000000L);
    }
}
