package de.hwrberlin.bidhub;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Die Klasse SocketInfo enthält Informationen zum WebSocket-Server, wie Host und Port,
 * und stellt Methoden zur Verfügung, um diese Informationen abzurufen und eine Verbindungs-URI zu erstellen.
 */
public abstract class SocketInfo {
    private static final String host = "85.215.149.95";
    private static final Integer port = 80;

    /**
     * Gibt den Host des WebSocket-Servers zurück.
     *
     * @return der Host des WebSocket-Servers
     */
    public static String getHost(){
        return host;
    }

    /**
     * Gibt den Port des WebSocket-Servers zurück.
     *
     * @return der Port des WebSocket-Servers
     */
    public static Integer getPort(){
        return port;
    }

    /**
     * Erstellt und gibt die Verbindungs-URI für den WebSocket-Server zurück.
     *
     * @return die Verbindungs-URI
     * @throws URISyntaxException wenn die URI-Syntax ungültig ist
     */
    public static URI getConnectionURI() throws URISyntaxException {
        return new URI("ws://" + host + ":" + port);
    }
}
