namespace Training.Module5.Collapse.Before;

public class LegacyNotificationFormatter
{
    public string Format(string recipient, string message)
    {
        ArgumentNullException.ThrowIfNull(recipient);
        ArgumentNullException.ThrowIfNull(message);

        return "To: " + recipient + "\nMessage: " + message;
    }
}
