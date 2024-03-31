package de.hwrberlin.bidhub.model.shared;

public class ApplicationClient {
    private final String username;
    private final long id;
    private String currentConnectedRoom = "";

    public ApplicationClient(String username, long id){
        this.username = username;
        this.id = id;
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
