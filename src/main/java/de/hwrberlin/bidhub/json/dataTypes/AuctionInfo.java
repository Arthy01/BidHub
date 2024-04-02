package de.hwrberlin.bidhub.json.dataTypes;

import java.util.concurrent.TimeUnit;

public class AuctionInfo {
    private ProductInfo product;
    private final float startTime;
    private final TimeUnit timeUnit;
    private final float minimumIncrement;
    private final float minimumBid;
    private int remainingSeconds;
    private AuctionRoomBidData bidData = null;

    public AuctionInfo(ProductInfo product, float startTime, TimeUnit timeUnit, float minimumIncrement, float minimumBid) {
        this.product = product;
        this.startTime = startTime;
        this.timeUnit = timeUnit;
        this.minimumIncrement = minimumIncrement;
        this.minimumBid = minimumBid;

        convertStartTimeToRemainingSeconds();
    }

    private void convertStartTimeToRemainingSeconds(){
        if (timeUnit == TimeUnit.SECONDS){
            remainingSeconds = Math.round(startTime);
        }
        else{
            remainingSeconds = Math.round(startTime * 60);
        }
    }

    public ProductInfo getProduct() {
        return product;
    }

    public float getStartTime() {
        return startTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public float getMinimumIncrement() {
        return minimumIncrement;
    }

    public float getMinimumBid() {
        return minimumBid;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
    public int reduceRemainingSeconds(){
        remainingSeconds = Math.clamp(remainingSeconds - 1, 0, Integer.MAX_VALUE);
        return remainingSeconds;
    }

    public AuctionRoomBidData getBidData() {
        return bidData;
    }

    public void setBidData(AuctionRoomBidData bidData) {
        this.bidData = bidData;
    }
    public void setProductID(long id){
        product = new ProductInfo(product.title(), product.description(), product.seller(), id);
    }
}
