namespace Training.Module5.Stage1;

public sealed class SmsNotification : Notification
{
    public SmsNotification(
        string messageId,
        string senderId,
        string body,
        bool deliveryReceipt)
        : base(messageId, senderId, body, deliveryReceipt)
    {
    }

    protected override string Channel() => "SMS";

    public override string Dispatch(bool successful)
    {
        string result = DispatchResult(successful);
        return successful ? AppendReceipt(result) : result;
    }
}
