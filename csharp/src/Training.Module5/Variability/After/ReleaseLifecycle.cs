namespace Training.Module5.Variability.After;

/// <summary>
/// State: obiekt sam zmienia zachowanie, przechodząc między stanami. Klient wywołuje
/// zawsze te same metody i nie wybiera wariantu.
/// </summary>
public sealed class ReleaseLifecycle
{
    private State _state = State.Planned;

    public ReleaseStatus Status => _state.Status;

    public void Advance() => _state = _state.Advance();

    public void Rollback() => _state = _state.Rollback();

    public string Banner => _state.Banner;

    // Java: enum with constant-specific bodies -> class with static instances.
    private abstract class State(ReleaseStatus status, string banner)
    {
        public static readonly State Planned = new PlannedState();
        public static readonly State Deploying = new DeployingState();
        public static readonly State Live = new LiveState();
        public static readonly State RolledBack = new RolledBackState();

        public ReleaseStatus Status { get; } = status;

        public string Banner { get; } = banner;

        public virtual State Advance() =>
            throw new InvalidOperationException("cannot advance release in state " + Status);

        public virtual State Rollback() =>
            throw new InvalidOperationException("cannot roll back release in state " + Status);
    }

    private sealed class PlannedState() : State(ReleaseStatus.Planned, "release planned")
    {
        public override State Advance() => Deploying;
    }

    private sealed class DeployingState() : State(ReleaseStatus.Deploying, "deployment in progress")
    {
        public override State Advance() => Live;

        public override State Rollback() => RolledBack;
    }

    private sealed class LiveState() : State(ReleaseStatus.Live, "release is live")
    {
        public override State Rollback() => RolledBack;
    }

    private sealed class RolledBackState() : State(ReleaseStatus.RolledBack, "release rolled back");
}
