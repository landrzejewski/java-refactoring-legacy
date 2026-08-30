package pl.training.module5.stage1;

public final class EmailNotification extends Notification {
    public EmailNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body, deliveryReceipt);
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
