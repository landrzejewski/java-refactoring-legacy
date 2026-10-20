package pl.training.module5.chain;

import java.util.Objects;

public record Decision(Approver approver, String reason) {
    public Decision {
        Objects.requireNonNull(approver, "approver");
        Objects.requireNonNull(reason, "reason");
    }

    public static Decision of(Approver approver, String reason) {
        return new Decision(approver, reason);
    }

    public enum Approver {
        AUTOMATIC,
        TEAM_LEAD,
        RELEASE_MANAGER,
        CHANGE_ADVISORY_BOARD,
        REJECTED
    }
}
