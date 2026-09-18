namespace Training.Module5.Tests;

public sealed class NotificationHierarchyEquivalenceTest
{
    [Fact]
    public void AllStagesPreserveEmailContractForEveryLegacyFlagAndStatus()
    {
        foreach (bool deliveryReceipt in new[] { false, true })
        {
            foreach (bool successful in new[] { false, true })
            {
                string[] results =
                [
                    new Training.Module5.Stage0.EmailNotification(
                        " msg-1 ", " ops ", " Deployment ready ", deliveryReceipt)
                        .Dispatch(successful),
                    new Training.Module5.Stage1.EmailNotification(
                        " msg-1 ", " ops ", " Deployment ready ", deliveryReceipt)
                        .Dispatch(successful),
                    new Training.Module5.Stage2.EmailNotification(
                        " msg-1 ", " ops ", " Deployment ready ", deliveryReceipt)
                        .Dispatch(successful),
                    new Training.Module5.Stage3.EmailNotification(
                        " msg-1 ", " ops ", " Deployment ready ", deliveryReceipt)
                        .Dispatch(successful)
                ];
                string expected = "msg-1|OPS|Deployment ready|EMAIL|"
                    + (successful ? "SENT" : "FAILED");

                AssertAllEqual(expected, results);
            }
        }
    }

    [Fact]
    public void AllStagesPreserveSmsContractForEveryReceiptAndStatus()
    {
        foreach (bool deliveryReceipt in new[] { false, true })
        {
            foreach (bool successful in new[] { false, true })
            {
                string[] results =
                [
                    new Training.Module5.Stage0.SmsNotification(
                        "msg-2", "ops", "Deploy now", deliveryReceipt)
                        .Dispatch(successful),
                    new Training.Module5.Stage1.SmsNotification(
                        "msg-2", "ops", "Deploy now", deliveryReceipt)
                        .Dispatch(successful),
                    new Training.Module5.Stage2.SmsNotification(
                        "msg-2", "ops", "Deploy now", deliveryReceipt)
                        .Dispatch(successful),
                    new Training.Module5.Stage3.SmsNotification(
                        "msg-2", "ops", "Deploy now", deliveryReceipt)
                        .Dispatch(successful)
                ];
                string expected = "msg-2|OPS|Deploy now|SMS|"
                    + (successful ? "SENT" : "FAILED")
                    + (successful && deliveryReceipt ? "|RECEIPT" : "");

                AssertAllEqual(expected, results);
            }
        }
    }

    [Fact]
    public void AllStagesPreserveValidationTypes()
    {
        AssertExceptionType<ArgumentException>(
        [
            () => new Training.Module5.Stage0.EmailNotification(" ", "ops", "body", false),
            () => new Training.Module5.Stage1.EmailNotification(" ", "ops", "body", false),
            () => new Training.Module5.Stage2.EmailNotification(" ", "ops", "body", false),
            () => new Training.Module5.Stage3.EmailNotification(" ", "ops", "body", false)
        ]);
        AssertExceptionType<ArgumentNullException>(
        [
            () => new Training.Module5.Stage0.SmsNotification("msg", null!, "body", false),
            () => new Training.Module5.Stage1.SmsNotification("msg", null!, "body", false),
            () => new Training.Module5.Stage2.SmsNotification("msg", null!, "body", false),
            () => new Training.Module5.Stage3.SmsNotification("msg", null!, "body", false)
        ]);
    }

    private static void AssertAllEqual(string expected, IEnumerable<string> results)
    {
        foreach (var result in results)
        {
            Assert.Equal(expected, result);
        }
    }

    // Assert.Throws<T> requires the exact type, so ArgumentNullException
    // (a subclass of ArgumentException) cannot silently satisfy the first check.
    private static void AssertExceptionType<T>(IEnumerable<Func<object>> scenarios)
        where T : Exception
    {
        foreach (var scenario in scenarios)
        {
            Assert.Equal(typeof(T), Assert.Throws<T>(scenario).GetType());
        }
    }
}
