import org.apache.commons.lang3.SystemUtils;
import update.updateException;

import java.math.BigInteger;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 06.03.2016.
 */
public class updateWrapper {
    public static final int MAIN_UPDATE = 0;
    public static final int TOOLS_UPDATE = 1;
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguimainupdate";

    private static final String DCRDWIN64_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsw64";
    private static final String DCRDWIN32_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsw32";

    private static final String DCRDLINUX64_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsl64";
    private static final String DCRDLINUX32_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsl32";
    private static final String DCRDLINUXARM_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolslarm";

    private static final String DCRDDARWIN64_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsd64";
    private static final String DCRDDARWIN32_URL = "https://raw.githubusercontent.com/SG-O/autoUpdate/master/dcrdjguitoolsd32";

    private static final int UNKNOWN_OS = 0;
    private static final int WINDOWS_OS = 1;
    private static final int LINUX_OS = 2;
    private static final int DARWIN_OS = 3;

    private static final int UNKNOWN_ARCH = 0;
    private static final int X32_ARCH = 1;
    private static final int X64_ARCH = 2;
    private static final int ARM_ARCH = 3;

    private static final BigInteger modulus = new BigInteger("C72E404930B0AD90DB785AF1F640A653075A6525EA6067D1949F80D722A1E9E9EF5F71EFFD0250524378ED25D7585E47502465B623961554A706AB755F7B82F6ED611F1190D7BD310DC04A797AA1FE327E5FE12E316DF0D217A2DB421441DF96FCA1F09E968B5F9FEB7E26AD94D40D9818882A209FCAB5D06B0E42996B69C718C8286908C73A32F229ED36B99BA2070FD3547F87B47562329067DFCABBF268622D8D020F49660468DC337758C0AD92D8D275854234E2E8817B3118F5AF45935825CCFC3F5057C940B2932A02E0637945D28314D66DC776F73121DF862610AA65056FDD6E6C37E5F0D75A22AD8942B4476CEE22A6B65FF94B48E325719C5DC1D9", 16); //This is the hard coded public key. DO NOT CHANGE unless you know what you are doing
    private static final BigInteger pubExp = new BigInteger("010001", 16);
    private int type;
    private update.update upd = null;

    public updateWrapper(int type, settings set) {
        this.type = type;
        String url;
        switch (type) {
            case TOOLS_UPDATE:
                url = getTools();
                if (set.isCustomBinariesUrl()) {
                    url = set.getCustomBinariesUrl();
                }
                break;
            default:
                url = UPDATE_URL;
                if (set.isCustomUpdateUrl()) {
                    url = set.getCustomUpdateUrl();
                }
                break;
        }
        System.out.println(url);
        try {
            upd = new update.update(modulus, pubExp, url);
        } catch (updateException e) {
            System.out.println(e);
        }

    }

    public static String getTools() {
        switch (getOsVersion()) {
            case WINDOWS_OS:
                switch (getArchType()) {
                    case X32_ARCH:
                        return DCRDWIN32_URL;
                    case X64_ARCH:
                        return DCRDWIN64_URL;
                    default:
                        return "";
                }
            case DARWIN_OS:
                switch (getArchType()) {
                    case X32_ARCH:
                        return DCRDDARWIN32_URL;
                    case X64_ARCH:
                        return DCRDDARWIN64_URL;
                    default:
                        return "";
                }
            case LINUX_OS:
                switch (getArchType()) {
                    case X32_ARCH:
                        return DCRDLINUX32_URL;
                    case X64_ARCH:
                        return DCRDLINUX64_URL;
                    case ARM_ARCH:
                        return DCRDLINUXARM_URL;
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    public static int getOsVersion() {
        if (SystemUtils.IS_OS_WINDOWS) return WINDOWS_OS;
        if (SystemUtils.IS_OS_LINUX) return LINUX_OS;
        if (SystemUtils.IS_OS_MAC) return DARWIN_OS;
        return UNKNOWN_OS;
    }

    public static int getArchType() {
        if (SystemUtils.IS_OS_WINDOWS) {
            if (System.getenv("ProgramFiles(x86)") != null) return X64_ARCH;
            if (System.getProperty("os.arch").contains("64")) return X64_ARCH;
            if (System.getProperty("os.arch").contains("86")) return X32_ARCH;
            return UNKNOWN_ARCH;
        } else {
            if (System.getProperty("os.arch").contains("64")) return X64_ARCH;
            if (System.getProperty("os.arch").contains("86")) return X32_ARCH;
            if (System.getProperty("os.arch").contains("arm")) return ARM_ARCH;
        }
        return UNKNOWN_ARCH;
    }

    public static boolean updatableOS(int type, settings set) {
        switch (type) {
            case MAIN_UPDATE:
                return true;
            case TOOLS_UPDATE:
                if (set.isCustomBinariesUrl()) return true;
                return (!getTools().isEmpty());
            default:
                return false;
        }
    }

    public boolean updateAvailable(long currentVersion) {
        if (upd == null) return false;
        return upd.updateAvailable(currentVersion, softwareInfo.getVersion());
    }

    public boolean run() {
        if (upd == null) return false;
        try {
            upd.run(softwareInfo.getVersion());
        } catch (updateException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
