using System.Globalization;

namespace Training.Module2.Tests;

public sealed class SeamedReminderServiceTest
{
    [Fact]
    public void SendsReminderForRenewalExactlySevenDaysAway()
    {
        TestableReminderService service = new(new DateOnly(2026, 8, 30));
        Subscription subscription = new(
            "developer@example.com",
            new DateOnly(2026, 9, 6));

        bool sent = service.SendRenewalReminder(subscription);

        Assert.True(sent);
        Assert.Equal(
            ["developer@example.com|2026-09-06"],
            service.SentMessages());
    }

    [Fact]
    public void DoesNotSendReminderMoreThanSevenDaysBeforeRenewal()
    {
        TestableReminderService service = new(new DateOnly(2026, 8, 30));
        Subscription subscription = new(
            "developer@example.com",
            new DateOnly(2026, 9, 7));

        bool sent = service.SendRenewalReminder(subscription);

        Assert.False(sent);
        Assert.Empty(service.SentMessages());
    }

    [Fact]
    public void DocumentsCurrentBehaviorForPastRenewalDate()
    {
        TestableReminderService service = new(new DateOnly(2026, 8, 30));
        Subscription subscription = new(
            "developer@example.com",
            new DateOnly(2026, 8, 29));

        bool sent = service.SendRenewalReminder(subscription);

        Assert.True(sent);
        Assert.Equal(
            ["developer@example.com|2026-08-29"],
            service.SentMessages());
    }

    private sealed class TestableReminderService(DateOnly today) : SeamedReminderService
    {
        private readonly List<string> messages = [];

        protected override DateOnly CurrentDate() => today;

        protected override void SendMessage(string email, DateOnly renewalDate) =>
            messages.Add(email + "|" + renewalDate.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture));

        public IReadOnlyList<string> SentMessages() => [.. messages];
    }
}
