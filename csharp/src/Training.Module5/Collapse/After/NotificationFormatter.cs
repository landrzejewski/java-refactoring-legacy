namespace Training.Module5.Collapse.After;

public sealed class NotificationFormatter
{
    public string Format(string recipient, string message)
    {
        ArgumentNullException.ThrowIfNull(recipient);
        ArgumentNullException.ThrowIfNull(message);

        return "To: " + recipient + "\nMessage: " + message;
    }
}
