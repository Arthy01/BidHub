package de.hwrberlin.bidhub.model.shared;

import de.hwrberlin.bidhub.model.shared.IClient;

import java.io.Serializable;

public class LoginInfo implements Serializable {
    private IClient client;
    private String username;
    private String password;

    public LoginInfo(IClient client, String username, String password) {
        this.client = client;
        this.username = username;
        this.password = password;
    }

    public IClient getClient() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
