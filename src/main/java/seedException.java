/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 05.03.2016.
 */
public class seedException extends Throwable {
    public static final int PGP_WORDLIST_NOT_LOADED = 1;
    public static final int INVALID_WORD = 2;
    public static final int EMPTY_SEED = 3;
    public static final int INVALID_HASH = 4;
    public static final int UNABLE_TO_HASH = 5;

    int exception = 0;
    long status = 0;
    String firstStatus = "";
    String secondStatus = "";

    public seedException(int exception) {
        this.exception = exception;
    }

    public seedException(int exception, long status) {
        this.exception = exception;
        this.status = status;
    }

    public seedException(int exception, long status, String firstStatus) {
        this.firstStatus = firstStatus;
        this.exception = exception;
        this.status = status;
    }

    public seedException(int exception, long status, String firstStatus, String secondStatus) {
        this.exception = exception;
        this.status = status;
        this.firstStatus = firstStatus;
        this.secondStatus = secondStatus;
    }

    public int getException() {
        return exception;
    }

    public long getStatus() {
        return status;
    }

    public String getFirstStatus() {
        return firstStatus;
    }

    public String getSecondStatus() {
        return secondStatus;
    }

    @Override
    public String toString() {
        switch (exception) {
            case PGP_WORDLIST_NOT_LOADED:
                return "PGP word list not loaded";
            case INVALID_WORD:
                return "Invalid word " + firstStatus;
            case EMPTY_SEED:
                return "The seed is empty";
            case INVALID_HASH:
                return "Invalid hash \"" + firstStatus + "\", expected \"" + secondStatus + "\"";
            case UNABLE_TO_HASH:
                return "Hashing failed";
            default:
                return "Error";
        }
    }
}
