package de.hwrberlin.bidhub.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AuctionRoomInfo implements Serializable {
    private String id;
    private String title;
    private String description;
    private String password;
    private final HashMap<String, Boolean> clients = new HashMap<>();

    public AuctionRoomInfo(String id, String title, String description, String password) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.password = password;
    }

    public void addClient(String username, Boolean isInitiator){
        clients.put(username, isInitiator);
    }
    public void removeClient(String username){
        clients.remove(username);
    }
    public HashMap<String, Boolean> getClients(){
        return clients;
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
        return clients.size();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
