import { type ChangeRequest, Risk } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';
import { ApprovalHandler } from './ApprovalHandler.js';

export class ReleaseManagerHandler extends ApprovalHandler {
  override handle(request: ChangeRequest): Decision {
    if (request.risk !== Risk.HIGH || !request.production) {
      return Decision.of(Approver.RELEASE_MANAGER, 'large or non-production change');
    }
    return this.passOn(request);
  }
}
