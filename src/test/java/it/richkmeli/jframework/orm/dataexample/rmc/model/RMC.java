package it.richkmeli.jframework.orm.dataexample.rmc.model;

import it.richkmeli.jframework.orm.annotation.Id;

public class RMC {

    @Id
    public String user;
    @Id
    public String rmcId;

    public RMC(String user, String clientID) {
        this.user = user;
        this.rmcId = clientID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRmcId() {
        return rmcId;
    }

    public void setRmcId(String rmcId) {
        this.rmcId = rmcId;
    }
}
