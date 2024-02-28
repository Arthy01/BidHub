package de.hwrberlin.bidhub;

public abstract class RMIInfo {
    private static final String host = "localhost";
    private static final Integer port = 1099;

    public static String getHost(){
        return host;
    }

    public static Integer getPort(){
        return port;
    }
}
