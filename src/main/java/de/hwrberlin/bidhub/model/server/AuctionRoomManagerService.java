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

public class AuctionRoomManagerService {
    private final ExecutorService roomsThreadPool;
    private final HashMap<String, AuctionRoomService> rooms = new HashMap<>();
    private final ArrayList<AuctionRoomInfo> cachedPrivateRooms = new ArrayList<>();
    private final ArrayList<AuctionRoomInfo> cachedPublicRooms = new ArrayList<>();
    private final Random random = new Random();

    public AuctionRoomManagerService(){
        roomsThreadPool = Executors.newCachedThreadPool();
        registerCallbacks();
        System.out.println("AuctionRoomManager Callbacks registriert!");
    }

    private void registerCallbacks(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_CreateAuctionRoom.name(), this::createAuctionRoom);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetAvailableRooms.name(), this::getAvailableRoomsRequest);
    }

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

    private String generateRandomId(int length) {
        int number = random.nextInt((int) Math.pow(10, length) - (int) Math.pow(10, length - 1)) + (int) Math.pow(10, length - 1);
        return "#" + String.format("%0" + length + "d", number);
    }

    private synchronized void getAvailableRoomsRequest(CallbackContext context){
        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new AvailableAuctionRoomsResponse(cachedPublicRooms), AvailableAuctionRoomsResponse.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());
    }
}
