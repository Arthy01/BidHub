package de.hwrberlin.bidhub.model.client;

public class ApplicationClient {
    private final String username;
    private String currentConnectedRoom = "";

    public ApplicationClient(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentConnectedRoom() {
        return currentConnectedRoom;
    }

    public void setCurrentConnectedRoom(String currentConnectedRoom) {
        this.currentConnectedRoom = currentConnectedRoom;
    }
}
