/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import java.io.*;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 31.01.2016.
 */
public class transaction {
    private String ID;
    private String block;
    private address address;
    private Coin amount;
    private Coin fee;
    private int confirmations;
    private String category;
    private long time;
    private String comment = "";

    //This is the internal representation of a transaction.
    public transaction(String ID, String block, address address, Coin amount, Coin fee, int confirmations, String category, long time) {
        this.ID = ID;
        this.block = block;
        this.address = address;
        this.amount = amount;
        this.fee = fee;
        this.confirmations = confirmations;
        this.category = category;
        try {
            loadTransaction();
        } catch (Exception e) {
        }
        this.time = time;
    }

    public String getID() {
        return ID;
    }

    public String getBlock() {
        return block;
    }

    public Coin getAmount() {
        return amount;
    }

    public address getAddress() {
        return address;
    }

    public Coin getFee() {
        return fee;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        try {
            saveTransaction();
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return "transaction{" +
                "ID='" + ID + '\'' +
                ", block='" + block + '\'' +
                ", address='" + address + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", confirmations=" + confirmations +
                ", category='" + category + '\'' +
                ", time=" + time +
                ", comment='" + comment + '\'' +
                '}';
    }

    /**
     * Save the comment for the current transaction
     * @throws Exception
     */
    public void saveTransaction() throws Exception{
        File setFile = new File(internal.storageTools.getSettingsDirectory(), ID + ".prop");
        if (!setFile.createNewFile()) return;
        if (!setFile.canWrite()) return;
        PrintWriter writer = new PrintWriter(setFile, "UTF-8");
        writer.print(comment);
        writer.close();
    }

    /**
     * Load the comment for the current transaction
     * @throws Exception
     */
    public void loadTransaction() throws Exception {
        File setFile = new File(internal.storageTools.getSettingsDirectory(), ID + ".prop");
        if (!setFile.exists()) return;
        if (!setFile.canRead()) return;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(setFile), "UTF-8"));
        String line;
        StringBuilder prop = new StringBuilder();
        while ((line = br.readLine()) != null) {
            prop.append(line);
        }
        this.comment = prop.toString();
    }
}
