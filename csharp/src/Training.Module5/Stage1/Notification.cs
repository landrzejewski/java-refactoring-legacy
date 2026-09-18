namespace Training.Module5.Stage1;

// Java: package-private abstract class. C# requires a base class to be at least as
// accessible as its public subclasses, so the type is public but can only be
// extended inside this assembly (private protected constructor).
public abstract class Notification
{
    private readonly string _senderId;
    private readonly string _body;
    private readonly bool _deliveryReceipt;

    private protected Notification(
        string messageId,
        string senderId,
        string body,
        bool deliveryReceipt)
    {
        MessageId = Normalized(messageId, "messageId");
        _senderId = Normalized(senderId, "senderId").ToUpperInvariant();
        _body = Normalized(body, "body");
        _deliveryReceipt = deliveryReceipt;
    }

    public string MessageId { get; }

    public string Summary()
    {
        return MessageId + "|" + _senderId + "|" + _body + "|" + Channel();
    }

    protected string DispatchResult(bool successful)
    {
        return Summary() + (successful ? "|SENT" : "|FAILED");
    }

    protected string AppendReceipt(string result)
    {
        return _deliveryReceipt ? result + "|RECEIPT" : result;
    }

    protected abstract string Channel();

    public abstract string Dispatch(bool successful);

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
