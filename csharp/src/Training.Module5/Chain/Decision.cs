namespace Training.Module5.Chain;

public sealed record Decision
{
    public Decision(Approver approver, string reason)
    {
        ArgumentNullException.ThrowIfNull(reason);
        Approver = approver;
        Reason = reason;
    }

    public Approver Approver { get; }

    public string Reason { get; }

    public static Decision Of(Approver approver, string reason) => new(approver, reason);
}

// Java nests this enum as Decision.Approver; C# forbids a nested type with the
// same name as the Approver property, so it lives next to the record.
public enum Approver
{
    Automatic,
    TeamLead,
    ReleaseManager,
    ChangeAdvisoryBoard,
    Rejected
}
