namespace Training.Module5.ExtractSubclass.After;

public class DeliveryJob
{
    internal DeliveryJob()
    {
    }

    public static DeliveryJob Immediate()
    {
        return new DeliveryJob();
    }

    public static DeliveryJob Scheduled(DateTimeOffset scheduledAt)
    {
        return new ScheduledDeliveryJob(scheduledAt);
    }

    public virtual string DispatchAt(DateTimeOffset now)
    {
        return "SENT";
    }
}
