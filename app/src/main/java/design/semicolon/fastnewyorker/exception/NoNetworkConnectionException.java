package design.semicolon.fastnewyorker.exception;

public class NoNetworkConnectionException extends Exception {

    private String reason;
    private String remedy;

    public String getReason() {
        return reason;
    }

    public String getRemedy() {
        return remedy;
    }

    public NoNetworkConnectionException() {
        super();
        this.reason = "Not connected to internet!";
        this.remedy = "Connect to internet and then try again";
    }

}
