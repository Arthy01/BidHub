package de.hwrberlin.bidhub.model.client;

public class ApplicationClient {
    private final String username;

    public ApplicationClient(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
