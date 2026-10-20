package pl.training.module5.chain.after;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

public final class SizeLimitHandler extends ApprovalHandler {
    @Override
    public Decision handle(ChangeRequest request) {
        if (request.linesChanged() > 5_000) {
            return Decision.of(Approver.REJECTED, "change too large");
        }
        return passOn(request);
    }
}
