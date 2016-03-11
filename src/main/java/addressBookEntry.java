import org.json.JSONObject;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 10.03.2016.
 */
public class addressBookEntry {
    private address adr;
    private String name;
    private String comment = "";

    public addressBookEntry(address adr, String name, String comment) {
        this.adr = adr;
        this.name = name;
        this.comment = comment;
    }

    public addressBookEntry(address adr, String name) {
        this.adr = adr;
        this.name = name;
    }

    public addressBookEntry(address adr) {
        this.adr = adr;
        this.name = adr.toString();
    }

    public addressBookEntry(JSONObject serialized, settings set) {
        this.adr = new address(serialized.optString("address", set.getDonationAddress().toString()), decredConstants.getNetConstants(set).getPubKeyHashAddrID().length);
        this.name = serialized.optString("name", adr.toString());
        this.comment = serialized.optString("comment", "");
    }

    public address getAdr() {
        return adr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        addressBookEntry that = (addressBookEntry) o;

        return adr != null ? adr.equals(that.adr) : that.adr == null;

    }

    @Override
    public int hashCode() {
        return adr != null ? adr.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "addressBookEntry{" +
                "address=" + adr +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public JSONObject serialize() {
        JSONObject serializer = new JSONObject();
        serializer.put("address", adr.toString());
        serializer.put("name", name);
        serializer.put("comment", comment);
        return serializer;
    }
}
