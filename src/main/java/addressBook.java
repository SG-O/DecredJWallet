import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 10.03.2016.
 */
public class addressBook {
    private HashMap<String, addressBookEntry> entries = new HashMap<String, addressBookEntry>();
    private String[] lastUsed = new String[3];
    private settings set;

    public addressBook(settings set) throws Exception {
        this.set = set;
        File setFile;
        if (set.isTestnet()) {
            setFile = new File(internal.storageTools.getSettingsDirectory(), "testNetAddressBook.prop");
        } else {
            setFile = new File(internal.storageTools.getSettingsDirectory(), "addressBook.prop");
        }
        if (!setFile.exists()) return;
        if (!setFile.canRead()) return;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(setFile), "UTF-8"));
        String line;
        StringBuilder prop = new StringBuilder();
        while ((line = br.readLine()) != null) {
            prop.append(line);
        }
        JSONObject content = new JSONObject(prop.toString());
        br.close();
        if (!content.has("entries")) return;
        JSONArray entries = content.getJSONArray("entries");
        for (int i = 0; i < entries.length(); i++) {
            addressBookEntry entry = new addressBookEntry(entries.optJSONObject(i), set);
            this.entries.put(entry.getName(), entry);
        }
        if (!content.has("lastUsed")) return;
        JSONArray lastUsed = content.getJSONArray("lastUsed");
        for (int i = 0; i < lastUsed.length(); i++) {
            if (i > 2) break;
            this.lastUsed[i] = lastUsed.getString(i);
        }
    }

    public address[] getLastUsed() {
        int i = 0;
        while (i < 3) {
            if (lastUsed[i] == null) break;
            i++;
        }
        address[] temp = new address[i];
        for (i = 0; i < temp.length; i++) {
            temp[i] = new address(lastUsed[i], decredConstants.getNetConstants(set).getPubKeyHashAddrID().length);
        }
        return temp;
    }

    public void setLastUsed(address entry) {
        for (int i = 0; i < lastUsed.length - 1; i++) {
            if (lastUsed[i] == null) break;
            lastUsed[i + 1] = lastUsed[i];
        }
        lastUsed[0] = entry.toString();
    }

    public addressBookEntry[] getAlphabetical() {
        if (entries.size() == 0) return new addressBookEntry[0];
        addressBookEntry[] temp = new addressBookEntry[entries.size()];
        Set<String> keySet = entries.keySet();
        String[] names = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
        if (temp.length != names.length) return null;
        for (int i = 0; i < names.length; i++) {
            temp[i] = entries.getOrDefault(names[i], null);
        }
        return temp;
    }

    public void addEntry(addressBookEntry entry) {
        this.entries.put(entry.getName(), entry);
    }

    public void removeEntry(addressBookEntry entry) {
        if (!this.entries.containsKey(entry.getName())) return;
        this.entries.remove(entry.getName());
    }

    public void saveAddressBook() {
        File setFile;
        if (set.isTestnet()) {
            setFile = new File(internal.storageTools.getSettingsDirectory(), "testNetAddressBook.prop");
        } else {
            setFile = new File(internal.storageTools.getSettingsDirectory(), "addressBook.prop");
        }

        if (!setFile.exists()) {
            try {
                if (!setFile.createNewFile()) return;
            } catch (IOException e) {
                return;
            }
        }
        JSONArray entries = new JSONArray();
        for (Map.Entry<String, addressBookEntry> entry : this.entries.entrySet()) {
            entries.put(entry.getValue().serialize());
        }
        JSONArray lastUsed = new JSONArray();
        for (int i = 0; i < this.lastUsed.length; i++) {
            if (this.lastUsed[i] == null) break;
            lastUsed.put(this.lastUsed[i]);
        }
        JSONObject content = new JSONObject();
        content.put("entries", entries);
        content.put("lastUsed", lastUsed);
        if (!setFile.canWrite()) return;
        try {
            PrintWriter writer = new PrintWriter(setFile, "UTF-8");
            writer.print(content.toString());
            writer.close();
        } catch (Exception e) {
            return;
        }
    }
}
