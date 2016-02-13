/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class parseUpdate {
    public static updateItem[] parse(JSONArray commands, File tempdir){
        if (commands == null) return new updateItem[0];
        updateItem[] ret = new updateItem[commands.length()];
        for(int i = 0; i < commands.length(); i++)
        {
            ret[i] = parseSingle(commands.getJSONObject(i), tempdir);
        }
        return ret;
    }

    public static updateItem parseSingle(JSONObject command, File tempdir){
        if (!command.has("type")) return new dummyUpdateItem();
        if (!command.has("ID")) return new dummyUpdateItem();
        int type = command.getInt("type");
        switch (type){
            case updateConstants.DOWNLOAD:
                return new downloadUpdateItem(command.getString("ID"),command.optString("url", ""),tempdir, command.optString("hash", ""));
            case updateConstants.MOVE:
                return new moveUpdateItem(command.getString("ID"), tempdir, command.optString("relativeSource", ""), command.optString("relativeDest", ""));
            case updateConstants.UNZIP:
                return new unzipUpdateItem(command.getString("ID"), tempdir, command.optString("relativeSource", ""), command.optString("relativeDest", ""));
            case updateConstants.EXECUTE:
                return new executeUpdateItem(command.getString("ID"));
            default:
                return  new dummyUpdateItem();
        }
    }
}
