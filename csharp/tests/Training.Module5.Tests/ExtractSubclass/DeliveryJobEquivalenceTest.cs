using System.Globalization;
using Training.Module5.ExtractSubclass.After;

namespace Training.Module5.Tests.ExtractSubclass;

public sealed class DeliveryJobEquivalenceTest
{
    private static readonly DateTimeOffset ScheduledAt =
        DateTimeOffset.Parse("2030-06-15T10:15:30Z", CultureInfo.InvariantCulture);

    public static TheoryData<string, DateTimeOffset, string> ScheduledDispatchScenarios() => new()
    {
        { "before the scheduled time", ScheduledAt.AddSeconds(-1), "WAITING_UNTIL 2030-06-15T10:15:30Z" },
        { "at the scheduled time", ScheduledAt, "SENT" },
        { "after the scheduled time", ScheduledAt.AddSeconds(1), "SENT" }
    };

    [Fact]
    public void ImmediateJobIsSentAtTheRequestedTime()
    {
        var now = DateTimeOffset.Parse("2029-01-01T00:00:00Z", CultureInfo.InvariantCulture);
        var before = Training.Module5.ExtractSubclass.Before.DeliveryJob.Immediate();
        var after = DeliveryJob.Immediate();

        Assert.Multiple(
            () => Assert.Equal("SENT", before.DispatchAt(now)),
            () => Assert.Equal("SENT", after.DispatchAt(now)));
    }

    [Theory]
    [MemberData(nameof(ScheduledDispatchScenarios))]
    public void ScheduledJobPreservesItsObservableResult(
        string scenario,
        DateTimeOffset now,
        string expectedResult)
    {
        Assert.NotEmpty(scenario);
        var before = Training.Module5.ExtractSubclass.Before.DeliveryJob.Scheduled(ScheduledAt);
        var after = DeliveryJob.Scheduled(ScheduledAt);

        Assert.Multiple(
            () => Assert.Equal(expectedResult, before.DispatchAt(now)),
            () => Assert.Equal(expectedResult, after.DispatchAt(now)),
            () => Assert.Equal(before.DispatchAt(now), after.DispatchAt(now)));
    }
}
