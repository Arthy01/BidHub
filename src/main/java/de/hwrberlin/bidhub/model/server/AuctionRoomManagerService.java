package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.*;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Verwaltet Auktionsräume innerhalb der Anwendung.
 * Diese Klasse ist verantwortlich für die Erstellung, Verwaltung und Bereitstellung von Informationen über Auktionsräume.
 * Sie interagiert mit dem WebSocket-Server, um Auktionsraumanfragen zu bearbeiten und entsprechende Aktionen auszuführen.
 */
public class AuctionRoomManagerService {
    private final ExecutorService roomsThreadPool;
    private final HashMap<String, AuctionRoomService> rooms = new HashMap<>();
    private final ArrayList<AuctionRoomInfo> cachedPrivateRooms = new ArrayList<>();
    private final ArrayList<AuctionRoomInfo> cachedPublicRooms = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Konstruktor für den AuctionRoomManagerService.
     * Initialisiert den ThreadPool für die Verwaltung von Auktionsräumen, registriert Callbacks für die Auktionsraum-Ereignisse und gibt eine Meldung aus, dass die Callbacks registriert wurden.
     */
    public AuctionRoomManagerService(){
        roomsThreadPool = Executors.newCachedThreadPool();
        registerCallbacks();
        System.out.println("AuctionRoomManager Callbacks registriert!");
    }

    /**
     * Registriert Callbacks für die Verarbeitung von Auktionsraumanfragen.
     * Diese Methode bindet spezifische Callbacks an Ereignisse, die mit der Erstellung und Abfrage von Auktionsräumen zusammenhängen.
     * Sie ermöglicht es dem AuctionRoomManagerService, auf Anfragen zur Erstellung neuer Auktionsräume und zur Abfrage verfügbarer Räume zu reagieren.
     */
    private void registerCallbacks(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_CreateAuctionRoom.name(), this::createAuctionRoom);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetAvailableRooms.name(), this::getAvailableRoomsRequest);
    }

    /**
     * Erstellt einen neuen Auktionsraum basierend auf den übergebenen Daten.
     * Diese Methode generiert eine eindeutige Raum-ID, erstellt einen neuen Auktionsraum und fügt ihn der Verwaltung hinzu.
     * Bei erfolgreicher Erstellung wird eine positive Antwort an den Client gesendet, andernfalls eine Fehlermeldung.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und die Verbindungsinformationen.
     */
    public synchronized void createAuctionRoom(CallbackContext context){
        String roomId = generateUniqueId();

        AuctionRoomCreateRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            AuctionRoomCreateResponseData responseData = new AuctionRoomCreateResponseData(false, null);
            JsonMessage msg = new JsonMessage(
                    CallbackType.Client_Response.name(),
                    responseData,
                    AuctionRoomCreateResponseData.class.getName()).setResponseId(context.message().getMessageId());
            context.conn().send(msg.toJson());
            throw new RuntimeException(e);
        }

        AuctionRoomInfo info = new AuctionRoomInfo(roomId, data.title(), data.description(), data.hashedPassword());
        AuctionRoomService room = new AuctionRoomService(info);

        room.addCloseRoomHook(() -> {
            synchronized (rooms){
                cachedPrivateRooms.remove(rooms.get(roomId).getInfo());
                cachedPublicRooms.remove(rooms.get(roomId).getInfo());

                rooms.remove(roomId);
            }
        });

        rooms.put(roomId, room);

        if (rooms.get(roomId).getInfo().getPassword().isBlank())
            cachedPublicRooms.add(info);
        else
            cachedPrivateRooms.add(info);

        roomsThreadPool.submit(room::start);

        AuctionRoomCreateResponseData responseData = new AuctionRoomCreateResponseData(true, info);
        JsonMessage msg = new JsonMessage(
                CallbackType.Client_Response.name(),
                responseData,
                AuctionRoomCreateResponseData.class.getName()).setResponseId(context.message().getMessageId());

        context.conn().send(msg.toJson());
    }

    /**
     * Generiert eine eindeutige ID für den Auktionsraum.
     * Diese Methode versucht, eine eindeutige ID zu generieren, indem sie eine zufällige Nummer erstellt und überprüft,
     * ob diese bereits verwendet wird. Bei Bedarf wird die Länge der ID erhöht, um Eindeutigkeit zu gewährleisten.
     *
     * @return Eine eindeutige ID als String.
     */
    private String generateUniqueId() {
        int length = 3;
        String uniqueId;
        int attempts = 0;

        while (true) {
            uniqueId = generateRandomId(length);

            synchronized (rooms) {
                if (!uniqueId.equals("000") && !rooms.containsKey(uniqueId)) {
                    return uniqueId;
                }
            }

            attempts++;
            if (attempts >= 3) {
                length++;
                attempts = 0;
            }
        }
    }

    /**
     * Generiert eine zufällige ID basierend auf der angegebenen Länge.
     * Diese Methode erstellt eine zufällige Nummer innerhalb des spezifizierten Bereichs und formatiert sie als String.
     *
     * @param length Die Länge der zu generierenden ID.
     * @return Eine zufällige ID als String.
     */
    private String generateRandomId(int length) {
        int number = random.nextInt((int) Math.pow(10, length) - (int) Math.pow(10, length - 1)) + (int) Math.pow(10, length - 1);
        return "#" + String.format("%0" + length + "d", number);
    }

    /**
     * Verarbeitet Anfragen zur Abfrage verfügbarer Auktionsräume.
     * Diese Methode sammelt Informationen über alle öffentlichen Auktionsräume und sendet diese als Antwort zurück.
     * Die Antwort enthält eine Liste von Auktionsrauminformationen, die für alle Benutzer zugänglich sind.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und die Verbindungsinformationen.
     */
    private synchronized void getAvailableRoomsRequest(CallbackContext context){
        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new AvailableAuctionRoomsResponse(cachedPublicRooms), AvailableAuctionRoomsResponse.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());
    }
}
