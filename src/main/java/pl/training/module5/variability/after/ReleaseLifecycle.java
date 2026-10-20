package pl.training.module5.variability.after;

import pl.training.module5.variability.ReleaseStatus;

/**
 * State: obiekt sam zmienia zachowanie, przechodząc między stanami. Klient wywołuje
 * zawsze te same metody i nie wybiera wariantu.
 */
public final class ReleaseLifecycle {
    private State state = State.PLANNED;

    public ReleaseStatus status() {
        return state.status;
    }

    public void advance() {
        state = state.advance();
    }

    public void rollback() {
        state = state.rollback();
    }

    public String banner() {
        return state.banner;
    }

    private enum State {
        PLANNED(ReleaseStatus.PLANNED, "release planned") {
            @Override
            State advance() {
                return DEPLOYING;
            }
        },
        DEPLOYING(ReleaseStatus.DEPLOYING, "deployment in progress") {
            @Override
            State advance() {
                return LIVE;
            }

            @Override
            State rollback() {
                return ROLLED_BACK;
            }
        },
        LIVE(ReleaseStatus.LIVE, "release is live") {
            @Override
            State rollback() {
                return ROLLED_BACK;
            }
        },
        ROLLED_BACK(ReleaseStatus.ROLLED_BACK, "release rolled back");

        private final ReleaseStatus status;
        private final String banner;

        State(ReleaseStatus status, String banner) {
            this.status = status;
            this.banner = banner;
        }

        State advance() {
            throw new IllegalStateException("cannot advance release in state " + status);
        }

        State rollback() {
            throw new IllegalStateException("cannot roll back release in state " + status);
        }
    }
}
