package pl.training.module5.stage1;

public final class SmsNotification extends Notification {
    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body, deliveryReceipt);
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
}
