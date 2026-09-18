namespace Training.Module5.Stage3;

public sealed class NotificationBatch
{
    public IReadOnlyList<string> DispatchAll(
        IReadOnlyList<IOutboundNotification> notifications,
        bool successful)
    {
        ArgumentNullException.ThrowIfNull(notifications);
        var validatedNotifications = notifications
            .Select(notification => notification
                ?? throw new ArgumentNullException(
                    nameof(notifications),
                    "notification must not be null"))
            .ToList();
        return validatedNotifications
            .Select(notification => notification.Dispatch(successful))
            .ToList()
            .AsReadOnly();
    }
}
