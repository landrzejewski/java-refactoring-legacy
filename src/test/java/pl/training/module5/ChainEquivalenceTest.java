package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.ChangeRequest.Risk;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;
import pl.training.module5.chain.after.ApprovalHandler;
import pl.training.module5.chain.after.ApprovalRouter;
import pl.training.module5.chain.before.LegacyApprovalRouter;

final class ChainEquivalenceTest {
    @Test
    void chainPreservesEveryLegacyRoutingDecision() {
        var before = new LegacyApprovalRouter();
        var after = ApprovalRouter.standard();
        int compared = 0;

        for (int lines : new int[] {0, 50, 51, 500, 501, 2_000, 2_001, 5_000, 5_001}) {
            for (Risk risk : Risk.values()) {
                for (boolean production : new boolean[] {true, false}) {
                    var request = new ChangeRequest("CR-" + lines, lines, risk, production);
                    assertEquals(before.route(request), after.route(request), request.toString());
                    compared++;
                }
            }
        }

        assertEquals(54, compared);
    }

    @Test
    void everyApproverIsReachable() {
        var router = ApprovalRouter.standard();
        List<Approver> approvers = new ArrayList<>();
        approvers.add(router.route(new ChangeRequest("a", 10, Risk.LOW, true)).approver());
        approvers.add(router.route(new ChangeRequest("b", 300, Risk.MEDIUM, true)).approver());
        approvers.add(router.route(new ChangeRequest("c", 900, Risk.MEDIUM, true)).approver());
        approvers.add(router.route(new ChangeRequest("d", 900, Risk.HIGH, true)).approver());
        approvers.add(router.route(new ChangeRequest("e", 3_000, Risk.HIGH, true)).approver());

        assertEquals(
                List.of(
                        Approver.AUTOMATIC,
                        Approver.TEAM_LEAD,
                        Approver.RELEASE_MANAGER,
                        Approver.CHANGE_ADVISORY_BOARD,
                        Approver.REJECTED),
                approvers);
    }

    @Test
    void exactlyOneHandlerHandlesTheRequest() {
        var passing = new CountingHandler(false);
        var handling = new CountingHandler(true);
        var neverReached = new CountingHandler(true);
        passing.then(handling).then(neverReached);

        Decision decision = new ApprovalRouter(passing)
                .route(new ChangeRequest("CR-1", 10, Risk.LOW, true));

        assertEquals(Decision.of(Approver.TEAM_LEAD, "handled"), decision);
        assertEquals(List.of(1, 1, 0),
                List.of(passing.calls, handling.calls, neverReached.calls));
    }

    @Test
    void endOfTheChainRejectsUnhandledRequests() {
        var only = new CountingHandler(false);

        assertEquals(
                Decision.of(Approver.REJECTED, "no approver for CR-2"),
                new ApprovalRouter(only).route(new ChangeRequest("CR-2", 10, Risk.LOW, true)));
        assertEquals(1, only.calls);
    }

    private static final class CountingHandler extends ApprovalHandler {
        private final boolean handles;
        private int calls;

        private CountingHandler(boolean handles) {
            this.handles = handles;
        }

        @Override
        public Decision handle(ChangeRequest request) {
            calls++;
            return handles ? Decision.of(Approver.TEAM_LEAD, "handled") : passOn(request);
        }
    }
}
