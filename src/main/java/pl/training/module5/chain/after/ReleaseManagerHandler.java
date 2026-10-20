package pl.training.module5.chain.after;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.ChangeRequest.Risk;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

public final class ReleaseManagerHandler extends ApprovalHandler {
    @Override
    public Decision handle(ChangeRequest request) {
        if (request.risk() != Risk.HIGH || !request.production()) {
            return Decision.of(Approver.RELEASE_MANAGER, "large or non-production change");
        }
        return passOn(request);
    }
}
