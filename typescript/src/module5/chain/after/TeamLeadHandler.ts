import { type ChangeRequest, Risk } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';
import { ApprovalHandler } from './ApprovalHandler.js';

export class TeamLeadHandler extends ApprovalHandler {
  override handle(request: ChangeRequest): Decision {
    if (request.risk !== Risk.HIGH && request.linesChanged <= 500) {
      return Decision.of(Approver.TEAM_LEAD, 'moderate change');
    }
    return this.passOn(request);
  }
}
