package update;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 06.03.2016.
 */
public class updateException extends Throwable {
    public static final int EMPTY_URL = 1;
    public static final int INFO_DOWNLOAD_ERROR = 2;
    public static final int INVALID_UPDATE_INFO = 3;
    public static final int NO_COMMANDS = 4;
    public static final int INVALID_SIGNATURE = 5;
    public static final int TEMP_DIR_ERROR = 6;
    public static final int UPDATE_FOR_FUTURE_VERSION = 7;
    public static final int COMMAND_EXECUTION_ERROR = 8;

    private int type = 0;

    public updateException(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case EMPTY_URL:
                return "URL is empty";
            case INFO_DOWNLOAD_ERROR:
                return "Could not download update information";
            case INVALID_UPDATE_INFO:
                return "Update information is invalid";
            case NO_COMMANDS:
                return "No commands available";
            case INVALID_SIGNATURE:
                return "Invalid update signature";
            case TEMP_DIR_ERROR:
                return "Could not create temporary directory";
            case UPDATE_FOR_FUTURE_VERSION:
                return "This update is for a future version";
            case COMMAND_EXECUTION_ERROR:
                return "Command could not be executed";
            default:
                return "Error " + type;
        }
    }
}
