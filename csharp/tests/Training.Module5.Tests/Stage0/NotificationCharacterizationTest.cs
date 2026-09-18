using Training.Module5.Stage0;

namespace Training.Module5.Tests.Stage0;

public sealed class NotificationCharacterizationTest
{
    [Fact]
    public void CapturesEmailBehavior()
    {
        var notification = new EmailNotification(
            " msg-1 ",
            " ops ",
            " Deployment ready ",
            true);

        Assert.Equal("msg-1", notification.MessageId);
        Assert.Equal("msg-1|OPS|Deployment ready|EMAIL", notification.Summary());
        Assert.Equal("msg-1|OPS|Deployment ready|EMAIL|SENT", notification.Dispatch(true));
        Assert.Equal("msg-1|OPS|Deployment ready|EMAIL|FAILED", notification.Dispatch(false));
    }

    [Fact]
    public void CapturesSmsReceiptBehavior()
    {
        var withReceipt = new SmsNotification("msg-2", "ops", "Deploy now", true);
        var withoutReceipt = new SmsNotification("msg-2", "ops", "Deploy now", false);

        Assert.Equal("msg-2|OPS|Deploy now|SMS|SENT|RECEIPT", withReceipt.Dispatch(true));
        Assert.Equal("msg-2|OPS|Deploy now|SMS|FAILED", withReceipt.Dispatch(false));
        Assert.Equal("msg-2|OPS|Deploy now|SMS|SENT", withoutReceipt.Dispatch(true));
    }

    [Fact]
    public void CapturesValidation()
    {
        Assert.Throws<ArgumentException>(
            () => new EmailNotification(" ", "ops", "body", false));
        Assert.Throws<ArgumentNullException>(
            () => new SmsNotification("msg", null!, "body", false));
    }
}
