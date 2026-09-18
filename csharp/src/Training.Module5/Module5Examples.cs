using Training.Module5.Stage3;

namespace Training.Module5;

public static class Module5Examples
{
    public static void Main(string[] args)
    {
        string legacyNotification =
            new Stage0.SmsNotification("msg-1", "ops", "Deployment ready", true)
                .Dispatch(true);
        string refactoredNotification =
            new SmsNotification("msg-1", "ops", "Deployment ready", true)
                .Dispatch(true);

        var scheduledAt = DateTimeOffset.Parse(
            "2030-06-15T10:15:30Z",
            System.Globalization.CultureInfo.InvariantCulture);
        var now = scheduledAt.AddSeconds(-1);
        string jobBefore = ExtractSubclass.Before.DeliveryJob
            .Scheduled(scheduledAt)
            .DispatchAt(now);
        string jobAfter = ExtractSubclass.After.DeliveryJob
            .Scheduled(scheduledAt)
            .DispatchAt(now);

        string formattedBefore = new Collapse.Before.NotificationFormatter()
            .Format("ops@example.com", "Deployment ready");
        string formattedAfter = new Collapse.After.NotificationFormatter()
            .Format("ops@example.com", "Deployment ready");

        var inheritedRecipients = new Composition.Before.RecipientList();
        var composedRecipients = new Composition.After.RecipientList();
        inheritedRecipients.Add("ops@example.com");
        composedRecipients.Add("ops@example.com");

        IReadOnlyList<IOutboundNotification> batch =
        [
            new EmailNotification("mail-1", "ops", "Ready"),
            new SmsNotification("sms-1", "ops", "Ready", true)
        ];

        Console.WriteLine("Hierarchy stages equivalent: "
            + JavaBoolean(legacyNotification == refactoredNotification));
        Console.WriteLine("Extract subclass equivalent: "
            + JavaBoolean(jobBefore == jobAfter));
        Console.WriteLine("Collapse hierarchy equivalent: "
            + JavaBoolean(formattedBefore == formattedAfter));
        Console.WriteLine("Composition client behavior equivalent: "
            + JavaBoolean(inheritedRecipients.Snapshot()
                .SequenceEqual(composedRecipients.Snapshot())));
        Console.WriteLine("Batch results: "
            + JavaList(new NotificationBatch().DispatchAll(batch, true)));
    }

    // Java prints booleans as "true"/"false"; C# bool.ToString() yields "True"/"False".
    private static string JavaBoolean(bool value) => value ? "true" : "false";

    // Mirrors java.util.List.toString(): "[a, b]".
    private static string JavaList(IEnumerable<string> values) => "[" + string.Join(", ", values) + "]";
}
