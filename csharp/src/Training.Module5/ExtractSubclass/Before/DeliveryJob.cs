namespace Training.Module5.ExtractSubclass.Before;

public sealed class DeliveryJob
{
    private const string Sent = "SENT";
    private const string WaitingUntil = "WAITING_UNTIL ";

    private readonly DateTimeOffset? _scheduledAt;

    private DeliveryJob(DateTimeOffset? scheduledAt)
    {
        _scheduledAt = scheduledAt;
    }

    public static DeliveryJob Immediate()
    {
        return new DeliveryJob(null);
    }

    public static DeliveryJob Scheduled(DateTimeOffset scheduledAt)
    {
        return new DeliveryJob(scheduledAt);
    }

    public string DispatchAt(DateTimeOffset now)
    {
        return _scheduledAt is { } instant && now < instant
            ? WaitingUntil + InstantFormat.ToIsoString(instant)
            : Sent;
    }
}
