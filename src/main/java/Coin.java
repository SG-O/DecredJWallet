/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 15.02.2016.
 */
public class Coin {
    private static final long FACTOR = 100000000L;
    private static final long MAX_AMOUNT = 2099999999800912L;
    private static final int ZEROS = Long.numberOfTrailingZeros(FACTOR);

    private long amount = 0;

    public Coin(double amount) throws Exception {
        setAmount(amount);
    }

    public Coin(String amount) throws Exception {
        setAmount(amount);
    }

    public double getAmount() {
        return ((double) amount / FACTOR);
    }

    public void setAmount(String amount) throws Exception {
        amount = amount.replace(',', '.');
        if (amount.startsWith(".")) {
            amount = "0" + amount;
        }
        String[] split = amount.split("\\.");
        if (split.length > 0) {
            if (split[0].length() > 9) throw new SecurityException();
        } else {
            throw new SecurityException();
        }
        setAmount(Double.parseDouble(amount));
    }

    public long getFixedPointAmount() {
        return amount;
    }

    public void setAmount(double amount) throws Exception {
        long temp = (long) (amount * FACTOR);
        if (temp > MAX_AMOUNT) throw new SecurityException();
        if (temp < (-MAX_AMOUNT)) throw new SecurityException();
        this.amount = temp;
    }

    @Override
    public String toString() {
        return String.format("%." + ZEROS + "f", getAmount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coin coin = (Coin) o;

        return amount == coin.amount;

    }

    @Override
    public int hashCode() {
        return (int) (amount ^ (amount >>> 32));
    }

    public void add(Coin co) throws Exception {
        if (co == null) return;
        long temp = this.amount + co.getFixedPointAmount();
        if (temp > MAX_AMOUNT) throw new SecurityException();
        if (temp < (-MAX_AMOUNT)) throw new SecurityException();
        this.amount = temp;
    }

    public void sub(Coin co) throws Exception {
        if (co == null) return;
        long temp = this.amount - co.getFixedPointAmount();
        if (temp > MAX_AMOUNT) throw new SecurityException();
        if (temp < (-MAX_AMOUNT)) throw new SecurityException();
        this.amount = temp;
    }

    public void mul(int factor) throws Exception {
        long temp = Math.multiplyExact(this.amount, (long) factor);
        if (temp > MAX_AMOUNT) throw new SecurityException();
        if (temp < (-MAX_AMOUNT)) throw new SecurityException();
        this.amount = temp;
    }
}
