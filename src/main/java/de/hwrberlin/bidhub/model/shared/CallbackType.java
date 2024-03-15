package de.hwrberlin.bidhub.model.shared;

public enum CallbackType {
    Client_Response,
    Server_ValidateLogin,
    Server_CreateAuctionRoom,
    Server_GetIsInitiator,
    Server_GetAuctionRoomInfo,
    Server_ReceiveChatMessageFromClient,
    Server_ValidateRoomPassword,
    Server_RegisterClient,
    Server_UnregisterClient,
    Server_GetAvailableRooms,
    Client_ReceiveChatMessage,
    Client_OnRoomClosed,
    Server_AuctionRoomKickClient,
    Server_AuctionRoomBanClient
}
