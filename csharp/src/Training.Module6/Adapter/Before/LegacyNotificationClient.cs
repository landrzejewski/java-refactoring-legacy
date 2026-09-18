namespace Training.Module6.Adapter.Before;

public sealed class LegacyNotificationClient
{
    public string NotifyUsingPreferred(
        ReleaseNotifier notifier,
        string? recipient,
        string? releaseId)
    {
        Validate(recipient, releaseId);
        ArgumentNullException.ThrowIfNull(notifier);
        return notifier(recipient!, releaseId!);
    }

    public string NotifyUsingLegacy(
        LegacyMessageGateway gateway,
        string? recipient,
        string? releaseId)
    {
        Validate(recipient, releaseId);
        ArgumentNullException.ThrowIfNull(gateway);
        return gateway(recipient!, "release:" + releaseId);
    }

    private static void Validate(string? recipient, string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(recipient))
        {
            throw new ArgumentException("recipient must not be blank");
        }
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
    }

    public delegate string ReleaseNotifier(string recipient, string releaseId);

    public delegate string LegacyMessageGateway(string destination, string body);
}
