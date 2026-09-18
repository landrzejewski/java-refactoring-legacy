namespace Training.Module2;

public sealed class ReminderService
{
    private readonly TimeProvider timeProvider;
    private readonly IReminderGateway reminderGateway;

    public ReminderService(TimeProvider timeProvider, IReminderGateway reminderGateway)
    {
        ArgumentNullException.ThrowIfNull(timeProvider);
        ArgumentNullException.ThrowIfNull(reminderGateway);
        this.timeProvider = timeProvider;
        this.reminderGateway = reminderGateway;
    }

    public bool SendRenewalReminder(Subscription subscription)
    {
        DateOnly today = DateOnly.FromDateTime(timeProvider.GetLocalNow().DateTime);

        if (subscription.RenewalDate > today.AddDays(7))
        {
            return false;
        }

        reminderGateway.Send(subscription.Email, subscription.RenewalDate);
        return true;
    }

    public interface IReminderGateway
    {
        void Send(string email, DateOnly renewalDate);
    }
}
