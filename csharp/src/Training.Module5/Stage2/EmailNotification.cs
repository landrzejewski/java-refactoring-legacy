namespace Training.Module5.Stage2;

public sealed class EmailNotification : Notification
{
    public EmailNotification(
        string messageId,
        string senderId,
        string body,
        bool ignoredDeliveryReceipt)
        : this(messageId, senderId, body)
    {
    }

    public EmailNotification(
        string messageId,
        string senderId,
        string body)
        : base(messageId, senderId, body)
    {
    }

    protected override string Channel() => "EMAIL";

    public override string Dispatch(bool successful) => DispatchResult(successful);
}
