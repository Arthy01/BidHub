package de.hwrberlin.bidhub;

public abstract class RMIInfo {
    private static String host = "212.227.233.17";
    private static Integer port = 1099;

    public static String getHost(){
        return host;
    }

    public static Integer getPort(){
        return port;
    }
}
