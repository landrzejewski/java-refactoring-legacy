package pl.training.module5.chain.before;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.ChangeRequest.Risk;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

/**
 * Długi łańcuch {@code if/else}: kolejność reguł jest istotna, ale ukryta w jednej metodzie.
 * Dodanie lub przestawienie zatwierdzającego wymaga edycji tej metody.
 */
public final class LegacyApprovalRouter {
    public Decision route(ChangeRequest request) {
        if (request.linesChanged() > 5_000) {
            return Decision.of(Approver.REJECTED, "change too large");
        } else if (request.risk() == Risk.LOW
                && (request.linesChanged() <= 50 || !request.production())) {
            return Decision.of(Approver.AUTOMATIC, "small low-risk change");
        } else if (request.risk() != Risk.HIGH && request.linesChanged() <= 500) {
            return Decision.of(Approver.TEAM_LEAD, "moderate change");
        } else if (request.risk() != Risk.HIGH || !request.production()) {
            return Decision.of(Approver.RELEASE_MANAGER, "large or non-production change");
        } else if (request.linesChanged() <= 2_000) {
            return Decision.of(Approver.CHANGE_ADVISORY_BOARD, "high-risk production change");
        } else {
            return Decision.of(Approver.REJECTED, "no approver for " + request.id());
        }
    }
}
