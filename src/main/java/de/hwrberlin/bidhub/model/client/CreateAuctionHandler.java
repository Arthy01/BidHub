package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.AuctionRoomCreateRequestData;
import de.hwrberlin.bidhub.json.dataTypes.AuctionRoomCreateResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.WaitForResponse;

/**
 * Behandelt die Erstellung von Auktionsräumen.
 * Diese Klasse ist verantwortlich für die Kommunikation mit dem Server, um neue Auktionsräume zu erstellen.
 * Sie sendet die erforderlichen Informationen an den Server und verarbeitet die Antwort.
 * Bei erfolgreicher Erstellung eines Auktionsraums wird automatisch dem Raum beigetreten.
 */
public class CreateAuctionHandler {
    /**
     * Erstellt einen neuen Auktionsraum mit den angegebenen Details.
     * Diese Methode sendet eine Anfrage an den Server, um einen Auktionsraum zu erstellen.
     * Die Methode verwendet {@link AuctionRoomCreateRequestData} für die Anfragedaten und erwartet
     * {@link AuctionRoomCreateResponseData} als Antwort. Bei Erfolg wird der neu erstellte Auktionsraum
     * automatisch beigetreten.
     *
     * @param title Der Titel des Auktionsraums.
     * @param description Die Beschreibung des Auktionsraums.
     * @param password Das Passwort für den Auktionsraum. Es wird gehasht, bevor es gesendet wird.
     * @throws RuntimeException Wenn ein Fehler bei der Verarbeitung der Antwort vom Server auftritt.
     */
    public void createAuction(String title, String description, String password){
        AuctionRoomCreateRequestData data = new AuctionRoomCreateRequestData(title, description, Helpers.hashPassword(password));
        JsonMessage msg = new JsonMessage(CallbackType.Server_CreateAuctionRoom.name(), data, AuctionRoomCreateRequestData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        AuctionRoomCreateResponseData responseData;

        try {
            responseData = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            throw new RuntimeException(e);
        }

        if (!responseData.success()){
            System.out.println("Der Raum konnte nicht erstellt werden!");
            return;
        }

        System.out.println("Auction Room erfolgreich erstellt!");
        JoinAuctionHandler.joinAuction(responseData.roomInfo().getId());
    }
}
