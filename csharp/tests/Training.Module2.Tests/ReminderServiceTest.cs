using System.Globalization;
using Microsoft.Extensions.Time.Testing;

namespace Training.Module2.Tests;

public sealed class ReminderServiceTest
{
    [Fact]
    public void UsesInjectedClockAndGateway()
    {
        FakeTimeProvider clock = new(
            DateTimeOffset.Parse("2026-08-30T10:00:00Z", CultureInfo.InvariantCulture));
        RecordingReminderGateway gateway = new();
        ReminderService service = new(clock, gateway);
        Subscription subscription = new(
            "developer@example.com",
            new DateOnly(2026, 9, 6));

        bool sent = service.SendRenewalReminder(subscription);

        Assert.True(sent);
        Assert.Equal(
            ["developer@example.com|2026-09-06"],
            gateway.Messages());
    }

    [Fact]
    public void DoesNotSendReminderMoreThanSevenDaysBeforeRenewal()
    {
        FakeTimeProvider clock = new(
            DateTimeOffset.Parse("2026-08-30T10:00:00Z", CultureInfo.InvariantCulture));
        RecordingReminderGateway gateway = new();
        ReminderService service = new(clock, gateway);
        Subscription subscription = new(
            "developer@example.com",
            new DateOnly(2026, 9, 7));

        bool sent = service.SendRenewalReminder(subscription);

        Assert.False(sent);
        Assert.Empty(gateway.Messages());
    }

    [Fact]
    public void PreservesCurrentBehaviorForPastRenewalDate()
    {
        FakeTimeProvider clock = new(
            DateTimeOffset.Parse("2026-08-30T10:00:00Z", CultureInfo.InvariantCulture));
        RecordingReminderGateway gateway = new();
        ReminderService service = new(clock, gateway);
        Subscription subscription = new(
            "developer@example.com",
            new DateOnly(2026, 8, 29));

        bool sent = service.SendRenewalReminder(subscription);

        Assert.True(sent);
        Assert.Equal(
            ["developer@example.com|2026-08-29"],
            gateway.Messages());
    }

    private sealed class RecordingReminderGateway : ReminderService.IReminderGateway
    {
        private readonly List<string> messages = [];

        public void Send(string email, DateOnly renewalDate) =>
            messages.Add(email + "|" + renewalDate.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture));

        public IReadOnlyList<string> Messages() => [.. messages];
    }
}
