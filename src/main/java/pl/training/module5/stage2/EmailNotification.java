package pl.training.module5.stage2;

public final class EmailNotification extends Notification {
    public EmailNotification(
            String messageId,
            String senderId,
            String body,
            boolean ignoredDeliveryReceipt) {
        this(messageId, senderId, body);
    }

    public EmailNotification(
            String messageId,
            String senderId,
            String body) {
        super(messageId, senderId, body);
    }

    @Override
    protected String channel() {
        return "EMAIL";
    }

    @Override
    public String dispatch(boolean successful) {
        return dispatchResult(successful);
    }
}
