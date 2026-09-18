namespace Training.Module7.Contract.After;

public sealed class DeploymentCapacity
{
    private readonly int _totalSlots;
    private int _remaining;

    public DeploymentCapacity(int totalSlots)
    {
        Contracts.Require(totalSlots >= 0, "totalSlots must not be negative");
        _totalSlots = totalSlots;
        _remaining = totalSlots;
        CheckInvariant();
        Contracts.Ensure(
            _remaining == totalSlots,
            "initial capacity must equal totalSlots");
    }

    public int Remaining
    {
        get
        {
            CheckInvariant();
            return _remaining;
        }
    }

    public void Reserve(int slots)
    {
        Contracts.Require(slots > 0, "slots must be positive");
        Contracts.Require(
            slots <= _remaining,
            "cannot reserve more slots than remain");

        var previousRemaining = _remaining;
        var nextRemaining = previousRemaining - slots;

        CheckInvariant(nextRemaining);
        _remaining = nextRemaining;
        Contracts.Ensure(
            _remaining == previousRemaining - slots,
            "reserve must reduce remaining capacity by slots");
        CheckInvariant();
    }

    public void Release(int slots)
    {
        Contracts.Require(slots > 0, "slots must be positive");
        Contracts.Require(
            slots <= _totalSlots - _remaining,
            "cannot release more slots than are reserved");

        var previousRemaining = _remaining;
        var nextRemaining = previousRemaining + slots;

        CheckInvariant(nextRemaining);
        _remaining = nextRemaining;
        Contracts.Ensure(
            _remaining == previousRemaining + slots,
            "release must increase remaining capacity by slots");
        CheckInvariant();
    }

    private void CheckInvariant() => CheckInvariant(_remaining);

    private void CheckInvariant(int candidateRemaining) =>
        Contracts.Invariant(
            candidateRemaining >= 0 && candidateRemaining <= _totalSlots,
            "remaining capacity must be between zero and totalSlots");
}
