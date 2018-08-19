package tech.nilu.bots.exception;

/**
 * Created by mmariameda on 3/27/17.
 */
public class ApplicationException extends RuntimeException {
    private String error;
    private String[] args;
    private  int status;


    public ApplicationException(int status, String error) {
        super(error);
        this.status = status;
        this.error = error;

    }

    public ApplicationException(int status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public ApplicationException(int status, String error, Throwable cause) {
        super(error, cause);
        this.status = status;
        this.error = error;
    }

    public ApplicationException(int status, String error, String[] args) {
        super(error);
        this.status = status;
        this.error = error;
        this.args = args;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
