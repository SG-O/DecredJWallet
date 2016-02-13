/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 05.02.2016.
 */
public abstract class updateItem {
    private String ID;
    private int type;

    public updateItem(String ID, int type) {
        this.ID = ID;
        this.type = type;
    }

    public abstract boolean execute();

    public String getID() {
        return ID;
    }

    public int getType() {
        return type;
    }

    public boolean check(){
        return true;
    }

    @Override
    public String toString() {
        return "updateItem{" +
                "ID='" + ID + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        updateItem that = (updateItem) o;

        if (type != that.type) return false;
        return ID != null ? ID.equals(that.ID) : that.ID == null;

    }

    @Override
    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }
}
