package pl.training.module5.chain.after;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.ChangeRequest.Risk;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

public final class TeamLeadHandler extends ApprovalHandler {
    @Override
    public Decision handle(ChangeRequest request) {
        if (request.risk() != Risk.HIGH && request.linesChanged() <= 500) {
            return Decision.of(Approver.TEAM_LEAD, "moderate change");
        }
        return passOn(request);
    }
}
