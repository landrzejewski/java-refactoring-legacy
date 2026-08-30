package pl.training.module5.stage3;

public final class SmsNotification extends Notification {
    private final boolean deliveryReceipt;

    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body);
        this.deliveryReceipt = deliveryReceipt;
    }

    @Override
    protected String channel() {
        return "SMS";
    }

    @Override
    public String dispatch(boolean successful) {
        String result = dispatchResult(successful);
        return successful ? appendReceipt(result) : result;
    }

    private String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }
}
