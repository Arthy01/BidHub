package de.hwrberlin.bidhub.model.shared;

import java.io.Serializable;

public class AuctionRoomInfo implements Serializable {
    private String id;
    private String title;
    private String description;
    private int currentClients;
    private String password;

    public AuctionRoomInfo(String id, String title, String description, String password) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.password = password;
        this.currentClients = 0;
    }

    public String getId() {
        return id;
    }
    public String getDisplayId(){
        return getId().split("_", 2)[1];
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentClients() {
        return currentClients;
    }

    public void setCurrentClients(int currentClients) {
        this.currentClients = currentClients;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
