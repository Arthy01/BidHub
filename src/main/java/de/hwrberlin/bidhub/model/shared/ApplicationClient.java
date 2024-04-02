package de.hwrberlin.bidhub.model.shared;

public class ApplicationClient {
    private final String username;
    private final long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String iban;
    private String currentConnectedRoom = "";

    public ApplicationClient(long id, String username, String email, String firstName, String lastName, String iban){
        this.username = username;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.iban = iban;
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

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIban() {
        return iban;
    }

    public String getEmail() {
        return email;
    }
}
