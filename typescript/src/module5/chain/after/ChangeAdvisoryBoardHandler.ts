import type { ChangeRequest } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';
import { ApprovalHandler } from './ApprovalHandler.js';

export class ChangeAdvisoryBoardHandler extends ApprovalHandler {
  override handle(request: ChangeRequest): Decision {
    if (request.linesChanged <= 2_000) {
      return Decision.of(Approver.CHANGE_ADVISORY_BOARD, 'high-risk production change');
    }
    return this.passOn(request);
  }
}
