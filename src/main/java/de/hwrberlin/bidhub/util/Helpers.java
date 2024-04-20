package de.hwrberlin.bidhub.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Helpers {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

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

    public static String getCurrentTime(){
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    public static float convertStringToFloat(String numberStr) throws NumberFormatException{
        String normalizedNumberStr = numberStr.replace(',', '.');

        return Float.parseFloat(normalizedNumberStr);
    }

    public static String formatToToEuro(String numberStr) {
        try {
            float number = convertStringToFloat(numberStr);
            return formatToEuro(number);
        } catch (NumberFormatException e) {
            return "{NaN}";
        }
    }

    public static String formatToEuro(float number){
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        return format.format(number) + " €";
    }

    public static boolean isEmailValid(String email){
        Matcher emailMatcher = emailPattern.matcher(email);
        return emailMatcher.matches();
    }

    public static boolean isIBANValid(String iban) {
        if (iban == null) {
            return false;
        }

        iban = iban.replaceAll("\\s", "");
        String regex = "^[A-Z]{2}\\d{2}[A-Z0-9]{11,29}$";

        return iban.matches(regex);
    }
}
