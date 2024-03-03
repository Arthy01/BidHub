package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomManagerService;
import de.hwrberlin.bidhub.util.Pair;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class AuctionRoomManagerService extends UnicastRemoteObject implements IAuctionRoomManagerService {
    private final Registry registry;
    private final HashMap<String, Pair<AuctionRoomService, AuctionRoomInfo>> rooms = new HashMap<>();
    private final Random random = new Random();

    public AuctionRoomManagerService(Registry registry) throws RemoteException {
        super();
        this.registry = registry;
    }

    @Override
    public String createAuctionRoom(String title, String description, String password) throws RemoteException {
        String roomId = "ARM_" + generateUniqueId();

        AuctionRoomInfo info = new AuctionRoomInfo(roomId, title, description, password);

        AuctionRoomService room = new AuctionRoomService(registry,info);
        registry.rebind(roomId, room);

        synchronized (rooms) { rooms.put(roomId, new Pair<>(room, info)); }
        room.addCloseRoomHook(() -> {synchronized (rooms) {rooms.remove(roomId);}});
        new Thread(room).start();

        System.out.println("Auktionsraum erstellt und gebunden: " + roomId);
        return roomId;
    }

    @Override
    public ArrayList<AuctionRoomInfo> getAuctionRoomInfos(){
        synchronized (rooms){
            ArrayList<AuctionRoomInfo> infos = new ArrayList<>();

            for (Pair<AuctionRoomService, AuctionRoomInfo> pair : rooms.values()){
                AuctionRoomInfo info = pair.getValue();

                if (!info.getPassword().isEmpty())
                    continue;

                info.setCurrentClients(pair.getKey().getClientCount());
                infos.add(info);
            }

            return infos;
        }
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
}
