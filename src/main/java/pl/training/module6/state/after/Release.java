package pl.training.module6.state.after;

public final class Release {
    private ReleaseState state = DraftState.INSTANCE;

    public Status status() {
        return state.status();
    }

    public void approve() {
        state = state.approve();
    }

    public void deploy() {
        state = state.deploy();
    }

    public void cancel() {
        state = state.cancel();
    }

    public enum Status {
        DRAFT,
        APPROVED,
        DEPLOYED,
        CANCELLED
    }

    private sealed interface ReleaseState
            permits DraftState, ApprovedState, DeployedState, CancelledState {
        Status status();

        default ReleaseState approve() {
            throw invalid("approve");
        }

        default ReleaseState deploy() {
            throw invalid("deploy");
        }

        default ReleaseState cancel() {
            throw invalid("cancel");
        }

        private IllegalStateException invalid(String action) {
            return new IllegalStateException(
                    "cannot " + action + " release in state " + status());
        }
    }

    private enum DraftState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.DRAFT;
        }

        @Override
        public ReleaseState approve() {
            return ApprovedState.INSTANCE;
        }

        @Override
        public ReleaseState cancel() {
            return CancelledState.INSTANCE;
        }
    }

    private enum ApprovedState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.APPROVED;
        }

        @Override
        public ReleaseState deploy() {
            return DeployedState.INSTANCE;
        }

        @Override
        public ReleaseState cancel() {
            return CancelledState.INSTANCE;
        }
    }

    private enum DeployedState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.DEPLOYED;
        }
    }

    private enum CancelledState implements ReleaseState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.CANCELLED;
        }
    }
}
