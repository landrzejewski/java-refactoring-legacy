import { describe, expect, it } from 'vitest';
import { ChangeRequest, Risk } from '../../src/module5/chain/ChangeRequest.js';
import { Approver, Decision } from '../../src/module5/chain/Decision.js';
import { ApprovalHandler } from '../../src/module5/chain/after/ApprovalHandler.js';
import { ApprovalRouter } from '../../src/module5/chain/after/ApprovalRouter.js';
import { LegacyApprovalRouter } from '../../src/module5/chain/before/LegacyApprovalRouter.js';

class CountingHandler extends ApprovalHandler {
  calls = 0;

  constructor(private readonly handles: boolean) {
    super();
  }

  override handle(request: ChangeRequest): Decision {
    this.calls++;
    return this.handles ? Decision.of(Approver.TEAM_LEAD, 'handled') : this.passOn(request);
  }
}

describe('ChainEquivalenceTest', () => {
  it('chainPreservesEveryLegacyRoutingDecision', () => {
    const before = new LegacyApprovalRouter();
    const after = ApprovalRouter.standard();
    let compared = 0;

    for (const lines of [0, 50, 51, 500, 501, 2_000, 2_001, 5_000, 5_001]) {
      for (const risk of Object.values(Risk)) {
        for (const production of [true, false]) {
          const request = new ChangeRequest('CR-' + lines, lines, risk, production);
          expect(after.route(request), request.toString()).toEqual(before.route(request));
          compared++;
        }
      }
    }

    expect(compared).toBe(54);
  });

  it('everyApproverIsReachable', () => {
    const router = ApprovalRouter.standard();
    const approvers: Approver[] = [];
    approvers.push(router.route(new ChangeRequest('a', 10, Risk.LOW, true)).approver);
    approvers.push(router.route(new ChangeRequest('b', 300, Risk.MEDIUM, true)).approver);
    approvers.push(router.route(new ChangeRequest('c', 900, Risk.MEDIUM, true)).approver);
    approvers.push(router.route(new ChangeRequest('d', 900, Risk.HIGH, true)).approver);
    approvers.push(router.route(new ChangeRequest('e', 3_000, Risk.HIGH, true)).approver);

    expect(approvers).toEqual([
      Approver.AUTOMATIC,
      Approver.TEAM_LEAD,
      Approver.RELEASE_MANAGER,
      Approver.CHANGE_ADVISORY_BOARD,
      Approver.REJECTED,
    ]);
  });

  it('exactlyOneHandlerHandlesTheRequest', () => {
    const passing = new CountingHandler(false);
    const handling = new CountingHandler(true);
    const neverReached = new CountingHandler(true);
    passing.then(handling).then(neverReached);

    const decision = new ApprovalRouter(passing).route(new ChangeRequest('CR-1', 10, Risk.LOW, true));

    expect(decision.equals(Decision.of(Approver.TEAM_LEAD, 'handled'))).toBe(true);
    expect([passing.calls, handling.calls, neverReached.calls]).toEqual([1, 1, 0]);
  });

  it('endOfTheChainRejectsUnhandledRequests', () => {
    const only = new CountingHandler(false);

    expect(
      new ApprovalRouter(only).route(new ChangeRequest('CR-2', 10, Risk.LOW, true))
        .equals(Decision.of(Approver.REJECTED, 'no approver for CR-2')),
    ).toBe(true);
    expect(only.calls).toBe(1);
  });
});
