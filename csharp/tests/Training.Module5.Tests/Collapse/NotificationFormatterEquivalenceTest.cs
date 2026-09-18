using Training.Module5.Collapse.After;

namespace Training.Module5.Tests.Collapse;

public sealed class NotificationFormatterEquivalenceTest
{
    public static TheoryData<string, string, string, string> Notifications() => new()
    {
        {
            "operational notification",
            "ops@example.com",
            "Deployment finished",
            "To: ops@example.com\nMessage: Deployment finished"
        },
        {
            "Unicode content",
            "zespół@example.pl",
            "Zażółć gęślą jaźń",
            "To: zespół@example.pl\nMessage: Zażółć gęślą jaźń"
        }
    };

    [Fact]
    public void CollapsedFormatterPreservesValidation()
    {
        var before = new Training.Module5.Collapse.Before.NotificationFormatter();
        var after = new Training.Module5.Collapse.After.NotificationFormatter();

        Assert.Multiple(
            () => Assert.Throws<ArgumentNullException>(() => before.Format(null!, "message")),
            () => Assert.Throws<ArgumentNullException>(() => after.Format(null!, "message")),
            () => Assert.Throws<ArgumentNullException>(() => before.Format("recipient", null!)),
            () => Assert.Throws<ArgumentNullException>(() => after.Format("recipient", null!)));
    }

    [Theory]
    [MemberData(nameof(Notifications))]
    public void CollapsedFormatterPreservesTheFormattedNotification(
        string scenario,
        string recipient,
        string message,
        string expectedNotification)
    {
        Assert.NotEmpty(scenario);
        var before = new Training.Module5.Collapse.Before.NotificationFormatter();
        var after = new NotificationFormatter();

        Assert.Multiple(
            () => Assert.Equal(expectedNotification, before.Format(recipient, message)),
            () => Assert.Equal(expectedNotification, after.Format(recipient, message)),
            () => Assert.Equal(before.Format(recipient, message), after.Format(recipient, message)));
    }
}
