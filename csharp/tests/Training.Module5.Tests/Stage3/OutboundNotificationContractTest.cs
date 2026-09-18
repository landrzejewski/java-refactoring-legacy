using Training.Module5.Stage3;

namespace Training.Module5.Tests.Stage3;

public sealed class OutboundNotificationContractTest
{
    [Fact]
    public void EveryImplementationSatisfiesTheExtractedContract()
    {
        IReadOnlyList<IOutboundNotification> notifications =
        [
            new EmailNotification("e-1", "ops", "Ready", false),
            new SmsNotification("s-1", "ops", "Ready", true)
        ];

        Assert.Equal(
            ["e-1|OPS|Ready|EMAIL|SENT", "s-1|OPS|Ready|SMS|SENT|RECEIPT"],
            notifications.Select(notification => notification.Dispatch(true)));
    }

    [Fact]
    public void BatchDependsOnlyOnTheClientRole()
    {
        var batch = new NotificationBatch();
        IReadOnlyList<IOutboundNotification> notifications =
        [
            new EmailNotification("e-1", "ops", "Ready"),
            new SmsNotification("s-1", "ops", "Ready", false)
        ];

        var results = batch.DispatchAll(notifications, false);

        Assert.Equal(
            ["e-1|OPS|Ready|EMAIL|FAILED", "s-1|OPS|Ready|SMS|FAILED"],
            results);
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<string>)results).Add("unexpected"));
    }

    [Fact]
    public void BatchRejectsInvalidInputs()
    {
        var batch = new NotificationBatch();
        var recordingNotification = new RecordingNotification();

        Assert.Throws<ArgumentNullException>(
            () => batch.DispatchAll(null!, true));
        Assert.Throws<ArgumentNullException>(
            () => batch.DispatchAll([recordingNotification, null!], true));
        Assert.Equal(0, recordingNotification.DispatchCalls);
    }

    // Java uses a lambda for the @FunctionalInterface; C# interfaces need a class.
    private sealed class RecordingNotification : IOutboundNotification
    {
        private int _dispatchCalls;

        public int DispatchCalls => _dispatchCalls;

        public string Dispatch(bool successful)
        {
            Interlocked.Increment(ref _dispatchCalls);
            return "dispatched";
        }
    }
}
