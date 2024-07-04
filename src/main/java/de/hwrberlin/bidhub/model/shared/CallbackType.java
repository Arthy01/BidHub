package de.hwrberlin.bidhub.model.shared;

/**
 * Definiert verschiedene Typen von Callbacks, die in der Anwendung verwendet werden, um die Kommunikation zwischen Client und Server zu steuern.
 * Diese Enumerationswerte repräsentieren spezifische Aktionen oder Anfragen, die zwischen dem Client und dem Server ausgetauscht werden können.
 */
public enum CallbackType {
    Client_Response,
    Server_ValidateLogin,
    Server_ValidateRegister,
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
    Server_AuctionRoomBanClient,
    Server_AuctionRoomStartAuction,
    Server_AuctionRoomOnBidRequest,
    Client_OnAuctionStarted,
    Client_OnAuctionFinished,
    Client_OnTick,
    Client_OnBid,
    Server_GetAuctionInfoRequest,
    Server_GetUserInformationRequest,
    Server_UpdateUserInformationRequest,
    Server_GetTransactionRequest
}
