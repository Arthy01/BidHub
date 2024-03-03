package de.hwrberlin.bidhub;

public abstract class RMIInfo {
    private static final String host = "25.50.242.248";// "85.215.149.95";
    private static final Integer port = 1099;

    public static String getHost(){
        return host;
    }

    public static Integer getPort(){
        return port;
    }
}
