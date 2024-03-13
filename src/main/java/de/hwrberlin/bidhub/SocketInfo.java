package de.hwrberlin.bidhub;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class SocketInfo {
    private static final String host = "85.215.149.95";
    private static final Integer port = 80;

    public static String getHost(){
        return host;
    }

    public static Integer getPort(){
        return port;
    }

    public static URI getConnectionURI() throws URISyntaxException {
        return new URI("ws://" + host + ":" + port);
    }
}
