using System.Globalization;

namespace Training.Module2;

public class SeamedReminderService
{
    public bool SendRenewalReminder(Subscription subscription)
    {
        DateOnly today = CurrentDate();

        if (subscription.RenewalDate > today.AddDays(7))
        {
            return false;
        }

        SendMessage(subscription.Email, subscription.RenewalDate);
        return true;
    }

    protected virtual DateOnly CurrentDate() =>
        DateOnly.FromDateTime(DateTime.UtcNow);

    protected virtual void SendMessage(string email, DateOnly renewalDate)
    {
        Console.WriteLine(string.Format(
            CultureInfo.InvariantCulture,
            "Sent renewal reminder to {0} for {1:yyyy-MM-dd}",
            email,
            renewalDate));
    }
}
