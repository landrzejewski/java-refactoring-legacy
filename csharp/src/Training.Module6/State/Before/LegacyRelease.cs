namespace Training.Module6.State.Before;

public sealed class LegacyRelease
{
    public Status Status { get; private set; } = Status.Draft;

    public void Approve()
    {
        if (Status == Status.Draft)
        {
            Status = Status.Approved;
            return;
        }
        throw Invalid("approve");
    }

    public void Deploy()
    {
        if (Status == Status.Approved)
        {
            Status = Status.Deployed;
            return;
        }
        throw Invalid("deploy");
    }

    public void Cancel()
    {
        if (Status == Status.Draft || Status == Status.Approved)
        {
            Status = Status.Cancelled;
            return;
        }
        throw Invalid("cancel");
    }

    private InvalidOperationException Invalid(string action) =>
        new("cannot " + action + " release in state " + Status);
}

// Java nests this enum as LegacyRelease.Status; C# forbids a nested type with
// the same name as the Status property, so it lives next to the class.
public enum Status
{
    Draft,
    Approved,
    Deployed,
    Cancelled
}
