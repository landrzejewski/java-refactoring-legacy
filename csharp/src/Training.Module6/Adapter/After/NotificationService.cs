namespace Training.Module6.Adapter.After;

public sealed class NotificationService
{
    private readonly IReleaseNotifier _notifier;

    public NotificationService(IReleaseNotifier notifier)
    {
        ArgumentNullException.ThrowIfNull(notifier);
        _notifier = notifier;
    }

    public string Notify(ReleaseMessage message)
    {
        ArgumentNullException.ThrowIfNull(message);
        return _notifier.Send(message);
    }
}
