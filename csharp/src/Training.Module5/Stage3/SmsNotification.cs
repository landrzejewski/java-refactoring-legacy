namespace Training.Module5.Stage3;

public sealed class SmsNotification : Notification
{
    private readonly bool _deliveryReceipt;

    public SmsNotification(
        string messageId,
        string senderId,
        string body,
        bool deliveryReceipt)
        : base(messageId, senderId, body)
    {
        _deliveryReceipt = deliveryReceipt;
    }

    protected override string Channel() => "SMS";

    public override string Dispatch(bool successful)
    {
        string result = DispatchResult(successful);
        return successful ? AppendReceipt(result) : result;
    }

    private string AppendReceipt(string result)
    {
        return _deliveryReceipt ? result + "|RECEIPT" : result;
    }
}
