namespace Training.Module5.Stage1;

public sealed class EmailNotification : Notification
{
    public EmailNotification(
        string messageId,
        string senderId,
        string body,
        bool deliveryReceipt)
        : base(messageId, senderId, body, deliveryReceipt)
    {
    }

    protected override string Channel() => "EMAIL";

    public override string Dispatch(bool successful) => DispatchResult(successful);
}
