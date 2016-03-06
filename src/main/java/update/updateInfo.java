package update;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 06.03.2016.
 */
public class updateInfo {
    private URL updateUrl;
    private long updateVersion = 0;
    private long minSoftwareVersion = 0;
    private boolean fallback = false;
    private URL fallbackUpdateUrl;

    public updateInfo(String url) throws updateException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            JSONObject upd = new JSONObject(in.readLine());
            in.close();
            if (!upd.has("Version")) throw new updateException(updateException.INVALID_UPDATE_INFO);
            updateVersion = upd.optLong("Version", 0);
            minSoftwareVersion = upd.optLong("MinimalSoftware", 0);
            if (!upd.has("Server")) throw new Exception();
            updateUrl = new URL(upd.optString("Server"));
            if (upd.has("Fallback")) {
                fallback = true;
                fallbackUpdateUrl = new URL(upd.optString("Fallback"));
            }
        } catch (Exception e) {
            throw new updateException(updateException.INFO_DOWNLOAD_ERROR);
        }
    }

    public updateInfo(URL updateUrl, long updateVersion, long minSoftwareVersion, URL fallbackUpdateUrl) {
        this.updateUrl = updateUrl;
        this.updateVersion = updateVersion;
        this.minSoftwareVersion = minSoftwareVersion;
        this.fallbackUpdateUrl = fallbackUpdateUrl;
        this.fallback = true;
    }

    public updateInfo(URL updateUrl, long updateVersion, long minSoftwareVersion) {
        this.updateUrl = updateUrl;
        this.updateVersion = updateVersion;
        this.minSoftwareVersion = minSoftwareVersion;
        this.fallback = false;
    }

    public updateInfo(URL updateUrl, long updateVersion) {
        this.updateUrl = updateUrl;
        this.updateVersion = updateVersion;
        this.fallback = false;
    }

    public URL getUpdateUrl() {
        return updateUrl;
    }

    public long getUpdateVersion() {
        return updateVersion;
    }

    public long getMinSoftwareVersion() {
        return minSoftwareVersion;
    }

    public URL getFallbackUpdateUrl() {
        return fallbackUpdateUrl;
    }

    public boolean isFallback() {
        return fallback;
    }

    public JSONObject gemerateJSON() throws updateException {
        JSONObject output = new JSONObject();
        if (updateUrl == null) throw new updateException(updateException.EMPTY_URL);
        if (updateUrl.toString() == "") throw new updateException(updateException.EMPTY_URL);
        output.put("Server", updateUrl.toString());
        if (fallback) {
            if (fallbackUpdateUrl == null) throw new updateException(updateException.EMPTY_URL);
            if (fallbackUpdateUrl.toString() == "") throw new updateException(updateException.EMPTY_URL);
            output.put("Server", fallbackUpdateUrl.toString());
        }
        output.put("Version", updateVersion);
        output.put("MinimalSoftware", minSoftwareVersion);
        return output;
    }

    public boolean updateAvailable(long currentVersion, long currentSoftware) {
        if (currentSoftware < minSoftwareVersion) return false;
        return currentVersion < updateVersion;
    }

    public updateCommands getCommands(long currentSoftware) throws updateException {
        BufferedReader in;
        if (currentSoftware < minSoftwareVersion) {
            if (!fallback) throw new updateException(updateException.UPDATE_FOR_FUTURE_VERSION);
            try {
                in = new BufferedReader(new InputStreamReader(fallbackUpdateUrl.openStream()));
            } catch (IOException e) {
                throw new updateException(updateException.INFO_DOWNLOAD_ERROR);
            }
        }
        try {
            in = new BufferedReader(new InputStreamReader(updateUrl.openStream()));
        } catch (IOException e) {
            throw new updateException(updateException.INFO_DOWNLOAD_ERROR);
        }
        try {
            return new updateCommands(new JSONObject(in.readLine()));
        } catch (IOException e) {
            throw new updateException(updateException.INFO_DOWNLOAD_ERROR);
        }
    }
}
