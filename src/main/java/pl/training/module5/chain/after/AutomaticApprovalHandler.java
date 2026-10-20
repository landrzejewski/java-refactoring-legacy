package pl.training.module5.chain.after;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.ChangeRequest.Risk;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

public final class AutomaticApprovalHandler extends ApprovalHandler {
    @Override
    public Decision handle(ChangeRequest request) {
        if (request.risk() == Risk.LOW
                && (request.linesChanged() <= 50 || !request.production())) {
            return Decision.of(Approver.AUTOMATIC, "small low-risk change");
        }
        return passOn(request);
    }
}
