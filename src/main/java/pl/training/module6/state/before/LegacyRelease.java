package pl.training.module6.state.before;

public final class LegacyRelease {
    private Status status = Status.DRAFT;

    public Status status() {
        return status;
    }

    public void approve() {
        if (status == Status.DRAFT) {
            status = Status.APPROVED;
            return;
        }
        throw invalid("approve");
    }

    public void deploy() {
        if (status == Status.APPROVED) {
            status = Status.DEPLOYED;
            return;
        }
        throw invalid("deploy");
    }

    public void cancel() {
        if (status == Status.DRAFT || status == Status.APPROVED) {
            status = Status.CANCELLED;
            return;
        }
        throw invalid("cancel");
    }

    private IllegalStateException invalid(String action) {
        return new IllegalStateException("cannot " + action + " release in state " + status);
    }

    public enum Status {
        DRAFT,
        APPROVED,
        DEPLOYED,
        CANCELLED
    }
}
