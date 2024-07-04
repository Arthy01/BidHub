package de.hwrberlin.bidhub.json.dataTypes;

import java.util.concurrent.TimeUnit;

/**
 * Die Klasse {@code AuctionInfo} enthält Informationen zu einer Auktion.
 */
public class AuctionInfo {
    private ProductInfo product;
    private final float startTime;
    private final TimeUnit timeUnit;
    private final float minimumIncrement;
    private final float minimumBid;
    private int remainingSeconds;
    private AuctionRoomBidData bidData = null;

    /**
     * Konstruktor für die Klasse {@code AuctionInfo}.
     *
     * @param product Das zu versteigernde Produkt.
     * @param startTime Die Startzeit der Auktion.
     * @param timeUnit Die Zeiteinheit der Startzeit (Sekunden oder Minuten).
     * @param minimumIncrement Die Mindeststeigerung des Gebots.
     * @param minimumBid Das Mindestgebot.
     */
    public AuctionInfo(ProductInfo product, float startTime, TimeUnit timeUnit, float minimumIncrement, float minimumBid) {
        this.product = product;
        this.startTime = startTime;
        this.timeUnit = timeUnit;
        this.minimumIncrement = minimumIncrement;
        this.minimumBid = minimumBid;

        convertStartTimeToRemainingSeconds();
    }

    /**
     * Konvertiert die Startzeit in verbleibende Sekunden.
     */
    private void convertStartTimeToRemainingSeconds(){
        if (timeUnit == TimeUnit.SECONDS){
            remainingSeconds = Math.round(startTime);
        }
        else{
            remainingSeconds = Math.round(startTime * 60);
        }
    }

    /**
     * Gibt das Produkt der Auktion zurück.
     *
     * @return Das Produkt der Auktion.
     */
    public ProductInfo getProduct() {
        return product;
    }

    /**
     * Gibt die Startzeit der Auktion zurück.
     *
     * @return Die Startzeit der Auktion.
     */
    public float getStartTime() {
        return startTime;
    }

    /**
     * Gibt die Zeiteinheit der Startzeit zurück.
     *
     * @return Die Zeiteinheit der Startzeit.
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Gibt die Mindeststeigerung des Gebots zurück.
     *
     * @return Die Mindeststeigerung des Gebots.
     */
    public float getMinimumIncrement() {
        return minimumIncrement;
    }

    /**
     * Gibt das Mindestgebot zurück.
     *
     * @return Das Mindestgebot.
     */
    public float getMinimumBid() {
        return minimumBid;
    }

    /**
     * Gibt die verbleibenden Sekunden bis zum Auktionsende zurück.
     *
     * @return Die verbleibenden Sekunden bis zum Auktionsende.
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * Setzt die verbleibenden Sekunden bis zum Auktionsende.
     *
     * @param remainingSeconds Die verbleibenden Sekunden bis zum Auktionsende.
     */
    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    /**
     * Verringert die verbleibenden Sekunden um eins.
     *
     * @return Die aktualisierte Anzahl der verbleibenden Sekunden.
     */
    public int reduceRemainingSeconds(){
        remainingSeconds = Math.clamp(remainingSeconds - 1, 0, Integer.MAX_VALUE);
        return remainingSeconds;
    }

    /**
     * Gibt die Gebotsdaten der Auktion zurück.
     *
     * @return Die Gebotsdaten der Auktion.
     */
    public AuctionRoomBidData getBidData() {
        return bidData;
    }

    /**
     * Setzt die Gebotsdaten der Auktion.
     *
     * @param bidData Die neuen Gebotsdaten der Auktion.
     */
    public void setBidData(AuctionRoomBidData bidData) {
        this.bidData = bidData;
    }

    /**
     * Setzt die Produkt-ID des zu versteigernden Produkts.
     *
     * @param id Die neue Produkt-ID.
     */
    public void setProductID(long id){
        product = new ProductInfo(product.title(), product.description(), product.seller(), id);
    }
}
