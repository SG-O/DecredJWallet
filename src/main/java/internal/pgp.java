package internal;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 08.03.2016.
 */
public class pgp {
    public static JSONArray getWordList() {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(pgp.class.getResourceAsStream("internal/pgpwl.json")));
        StringBuilder builder = new StringBuilder();
        String read;
        try {
            while ((read = inputStream.readLine()) != null) {
                builder.append(read);
            }
            JSONArray tmpArray = new JSONArray(builder.toString());
            if (tmpArray.length() != 256) return null;
            return tmpArray;
        } catch (Exception e) {
            return null;
        }
    }
}
