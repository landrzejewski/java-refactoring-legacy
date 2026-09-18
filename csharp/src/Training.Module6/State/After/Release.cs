namespace Training.Module6.State.After;

public sealed class Release
{
    private IReleaseState _state = DraftState.Instance;

    public Status Status => _state.Status;

    public void Approve() => _state = _state.Approve();

    public void Deploy() => _state = _state.Deploy();

    public void Cancel() => _state = _state.Cancel();

    private interface IReleaseState
    {
        Status Status { get; }

        IReleaseState Approve() => throw Invalid("approve");

        IReleaseState Deploy() => throw Invalid("deploy");

        IReleaseState Cancel() => throw Invalid("cancel");

        private InvalidOperationException Invalid(string action) =>
            new("cannot " + action + " release in state " + Status);
    }

    private sealed class DraftState : IReleaseState
    {
        public static readonly DraftState Instance = new();

        private DraftState()
        {
        }

        public Status Status => Status.Draft;

        public IReleaseState Approve() => ApprovedState.Instance;

        public IReleaseState Cancel() => CancelledState.Instance;
    }

    private sealed class ApprovedState : IReleaseState
    {
        public static readonly ApprovedState Instance = new();

        private ApprovedState()
        {
        }

        public Status Status => Status.Approved;

        public IReleaseState Deploy() => DeployedState.Instance;

        public IReleaseState Cancel() => CancelledState.Instance;
    }

    private sealed class DeployedState : IReleaseState
    {
        public static readonly DeployedState Instance = new();

        private DeployedState()
        {
        }

        public Status Status => Status.Deployed;
    }

    private sealed class CancelledState : IReleaseState
    {
        public static readonly CancelledState Instance = new();

        private CancelledState()
        {
        }

        public Status Status => Status.Cancelled;
    }
}

// Java nests this enum as Release.Status; C# forbids a nested type with the
// same name as the Status property, so it lives next to the class.
public enum Status
{
    Draft,
    Approved,
    Deployed,
    Cancelled
}
