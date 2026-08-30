package pl.training.module7.contract.after;

public final class DeploymentCapacity {
    private final int totalSlots;
    private int remaining;

    public DeploymentCapacity(int totalSlots) {
        Contracts.require(
                totalSlots >= 0,
                "totalSlots must not be negative");
        this.totalSlots = totalSlots;
        remaining = totalSlots;
        checkInvariant();
        Contracts.ensure(
                remaining == totalSlots,
                "initial capacity must equal totalSlots");
    }

    public void reserve(int slots) {
        Contracts.require(slots > 0, "slots must be positive");
        Contracts.require(
                slots <= remaining,
                "cannot reserve more slots than remain");

        int previousRemaining = remaining;
        int nextRemaining = previousRemaining - slots;

        checkInvariant(nextRemaining);
        remaining = nextRemaining;
        Contracts.ensure(
                remaining == previousRemaining - slots,
                "reserve must reduce remaining capacity by slots");
        checkInvariant();
    }

    public void release(int slots) {
        Contracts.require(slots > 0, "slots must be positive");
        Contracts.require(
                slots <= totalSlots - remaining,
                "cannot release more slots than are reserved");

        int previousRemaining = remaining;
        int nextRemaining = previousRemaining + slots;

        checkInvariant(nextRemaining);
        remaining = nextRemaining;
        Contracts.ensure(
                remaining == previousRemaining + slots,
                "release must increase remaining capacity by slots");
        checkInvariant();
    }

    public int remaining() {
        checkInvariant();
        return remaining;
    }

    private void checkInvariant() {
        checkInvariant(remaining);
    }

    private void checkInvariant(int candidateRemaining) {
        Contracts.invariant(
                candidateRemaining >= 0 && candidateRemaining <= totalSlots,
                "remaining capacity must be between zero and totalSlots");
    }
}
