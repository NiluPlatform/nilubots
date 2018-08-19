package tech.nilu.bots.dto.request;

import java.io.Serializable;

public class BaseRequest implements Serializable {

    protected String locale = "us_en";

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
