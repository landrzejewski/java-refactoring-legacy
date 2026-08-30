package pl.training.module7.contract.before;

public final class LegacyDeploymentCapacity {
    private int remaining;

    public LegacyDeploymentCapacity(int totalSlots) {
        remaining = totalSlots;
    }

    public void reserve(int slots) {
        remaining -= slots;
    }

    public void release(int slots) {
        remaining += slots;
    }

    public int remaining() {
        return remaining;
    }
}
