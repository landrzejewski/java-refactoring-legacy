import type { ChangeRequest } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';
import { ApprovalHandler } from './ApprovalHandler.js';

export class SizeLimitHandler extends ApprovalHandler {
  override handle(request: ChangeRequest): Decision {
    if (request.linesChanged > 5_000) {
      return Decision.of(Approver.REJECTED, 'change too large');
    }
    return this.passOn(request);
  }
}
