package de.hwrberlin.bidhub.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bietet Hilfsmethoden für allgemeine Aufgaben wie Passwort-Hashing, Zeitformatierung,
 * String-zu-Float-Konvertierung, Währungsformatierung und Validierung von E-Mail-Adressen und IBANs.
 */
public abstract class Helpers {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    /**
     * Erzeugt einen SHA-256 Hash eines gegebenen Passworts.
     *
     * @param password Das Passwort, das gehasht werden soll.
     * @return Ein hexadezimaler String des gehashten Passworts oder ein leerer String, falls ein Fehler auftritt.
     */
    public static String hashPassword(String password){
        if (password.isBlank())
            return "";

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedPassword = md.digest();

            // Konvertiere das Byte-Array in einen hexadezimalen String
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedPassword) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Gibt die aktuelle Uhrzeit im Format HH:mm:ss zurück.
     *
     * @return Ein String, der die aktuelle Uhrzeit repräsentiert.
     */
    public static String getCurrentTime(){
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * Konvertiert einen String in einen Float-Wert, wobei Kommas durch Punkte ersetzt werden, um das Format zu korrigieren.
     *
     * @param numberStr Der zu konvertierende String.
     * @return Der konvertierte Float-Wert.
     * @throws NumberFormatException Wenn der String nicht in einen Float-Wert konvertiert werden kann.
     */
    public static float convertStringToFloat(String numberStr) throws NumberFormatException{
        String normalizedNumberStr = numberStr.replace(',', '.');

        return Float.parseFloat(normalizedNumberStr);
    }

    /**
     * Formatiert einen String, der eine Zahl repräsentiert, zu einem Euro-Währungsformat.
     *
     * @param numberStr Der String, der die zu formatierende Zahl enthält.
     * @return Ein String im Euro-Währungsformat oder "{NaN}", falls eine NumberFormatException auftritt.
     */
    public static String formatToToEuro(String numberStr) {
        try {
            float number = convertStringToFloat(numberStr);
            return formatToEuro(number);
        } catch (NumberFormatException e) {
            return "{NaN}";
        }
    }

    /**
     * Formatiert eine gegebene Zahl zu einem String im Euro-Währungsformat.
     *
     * @param number Die zu formatierende Zahl.
     * @return Ein String, der die Zahl im Euro-Währungsformat darstellt.
     */
    public static String formatToEuro(float number){
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        return format.format(number) + " €";
    }

    /**
     * Überprüft, ob eine gegebene E-Mail-Adresse gültig ist, basierend auf einem vorgegebenen regulären Ausdruck.
     *
     * @param email Die zu überprüfende E-Mail-Adresse.
     * @return {@code true}, wenn die E-Mail-Adresse gültig ist, sonst {@code false}.
     */
    public static boolean isEmailValid(String email){
        Matcher emailMatcher = emailPattern.matcher(email);
        return emailMatcher.matches();
    }

    /**
     * Überprüft, ob eine gegebene IBAN gültig ist, basierend auf einem vorgegebenen regulären Ausdruck.
     *
     * @param iban Die zu überprüfende IBAN.
     * @return {@code true}, wenn die IBAN gültig ist, sonst {@code false}.
     */
    public static boolean isIBANValid(String iban) {
        if (iban == null) {
            return false;
        }

        iban = iban.replaceAll("\\s", "");
        String regex = "^[A-Z]{2}\\d{2}[A-Z0-9]{11,29}$";

        return iban.matches(regex);
    }
}
