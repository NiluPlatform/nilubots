package tech.nilu.bots.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import tech.nilu.bots.ActionCodes;
import tech.nilu.bots.exception.ApplicationException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by mmariameda on 3/29/17.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {
    protected int status = ActionCodes.TYPE_SUCCESS;
    protected String error;
    protected String message;

    public BaseResponse() {
    }

    public BaseResponse(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public BaseResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public BaseResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.status = ActionCodes.TYPE_GENERAL_ERROR;
    }

    public BaseResponse(Exception e){
        if ( e instanceof ApplicationException){
            this.error = ((ApplicationException) e).getError();
            this.status = ((ApplicationException) e).getStatus();
        } else {
            this.error = ActionCodes.UNKNOWN_ERROR;
            this.status = ActionCodes.TYPE_GENERAL_ERROR;
        }
        this.message = e.getMessage();

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
