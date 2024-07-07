package de.hwrberlin.bidhub.exceptions;

/**
 * Die InvalidInputException wird geworfen, wenn eine ungültige Eingabe erkannt wird.
 */
public class InvalidInputException extends Exception{
    private final String message;

    /**
     * Konstruktor für InvalidInputException.
     *
     * @param message die Fehlermeldung
     */
    public InvalidInputException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
