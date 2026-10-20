import { type ChangeRequest, Risk } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';
import { ApprovalHandler } from './ApprovalHandler.js';

export class AutomaticApprovalHandler extends ApprovalHandler {
  override handle(request: ChangeRequest): Decision {
    if (request.risk === Risk.LOW && (request.linesChanged <= 50 || !request.production)) {
      return Decision.of(Approver.AUTOMATIC, 'small low-risk change');
    }
    return this.passOn(request);
  }
}
