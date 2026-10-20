package pl.training.module5.chain.after;

import java.util.Objects;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.Decision;

/**
 * Miejsce, w którym łańcuch jest jawnie złożony. Kolejność ogniw jest widoczna
 * w jednym wyrażeniu i może być inna dla innego procesu zatwierdzania.
 */
public final class ApprovalRouter {
    private final ApprovalHandler chain;

    public ApprovalRouter(ApprovalHandler chain) {
        this.chain = Objects.requireNonNull(chain, "chain");
    }

    public static ApprovalRouter standard() {
        ApprovalHandler first = new SizeLimitHandler();
        first.then(new AutomaticApprovalHandler())
                .then(new TeamLeadHandler())
                .then(new ReleaseManagerHandler())
                .then(new ChangeAdvisoryBoardHandler());
        return new ApprovalRouter(first);
    }

    public Decision route(ChangeRequest request) {
        return chain.handle(Objects.requireNonNull(request, "request"));
    }
}
