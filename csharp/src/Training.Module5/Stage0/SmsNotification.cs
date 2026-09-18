namespace Training.Module5.Stage0;

public sealed class SmsNotification
{
    private readonly string messageId;
    private readonly string senderId;
    private readonly string body;
    private readonly bool deliveryReceipt;

    public SmsNotification(
        string messageId,
        string senderId,
        string body,
        bool deliveryReceipt)
    {
        this.messageId = Normalized(messageId, "messageId");
        this.senderId = Normalized(senderId, "senderId").ToUpperInvariant();
        this.body = Normalized(body, "body");
        this.deliveryReceipt = deliveryReceipt;
    }

    public string MessageId => messageId;

    public string Summary()
    {
        return messageId + "|" + senderId + "|" + body + "|SMS";
    }

    public string Dispatch(bool successful)
    {
        string result = Summary() + (successful ? "|SENT" : "|FAILED");
        return successful ? AppendReceipt(result) : result;
    }

    private string AppendReceipt(string result)
    {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }

    private static string Normalized(string value, string fieldName)
    {
        ArgumentNullException.ThrowIfNull(value, fieldName);
        string normalized = value.Trim();
        if (normalized.Length == 0)
        {
            throw new ArgumentException(fieldName + " must not be blank", fieldName);
        }
        return normalized;
    }
}
