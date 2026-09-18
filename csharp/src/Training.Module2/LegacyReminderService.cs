using System.Globalization;

namespace Training.Module2;

public sealed class LegacyReminderService
{
    public bool SendRenewalReminder(Subscription subscription)
    {
        DateOnly today = DateOnly.FromDateTime(DateTime.UtcNow);

        if (subscription.RenewalDate > today.AddDays(7))
        {
            return false;
        }

        Console.WriteLine(string.Format(
            CultureInfo.InvariantCulture,
            "Sent renewal reminder to {0} for {1:yyyy-MM-dd}",
            subscription.Email,
            subscription.RenewalDate));
        return true;
    }
}
