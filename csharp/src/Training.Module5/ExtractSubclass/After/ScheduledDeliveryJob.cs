namespace Training.Module5.ExtractSubclass.After;

public sealed class ScheduledDeliveryJob : DeliveryJob
{
    private readonly DateTimeOffset _scheduledAt;

    internal ScheduledDeliveryJob(DateTimeOffset scheduledAt)
    {
        _scheduledAt = scheduledAt;
    }

    public override string DispatchAt(DateTimeOffset now)
    {
        if (now < _scheduledAt)
        {
            return "WAITING_UNTIL " + InstantFormat.ToIsoString(_scheduledAt);
        }
        return base.DispatchAt(now);
    }
}
