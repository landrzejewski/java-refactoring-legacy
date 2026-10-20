package pl.training.module5.chain.after;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

public final class ChangeAdvisoryBoardHandler extends ApprovalHandler {
    @Override
    public Decision handle(ChangeRequest request) {
        if (request.linesChanged() <= 2_000) {
            return Decision.of(Approver.CHANGE_ADVISORY_BOARD, "high-risk production change");
        }
        return passOn(request);
    }
}
