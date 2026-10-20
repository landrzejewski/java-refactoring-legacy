import { type ChangeRequest, Risk } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';

/**
 * Długi łańcuch if/else: kolejność reguł jest istotna, ale ukryta w jednej metodzie.
 * Dodanie lub przestawienie zatwierdzającego wymaga edycji tej metody.
 */
export class LegacyApprovalRouter {
  route(request: ChangeRequest): Decision {
    if (request.linesChanged > 5_000) {
      return Decision.of(Approver.REJECTED, 'change too large');
    } else if (request.risk === Risk.LOW && (request.linesChanged <= 50 || !request.production)) {
      return Decision.of(Approver.AUTOMATIC, 'small low-risk change');
    } else if (request.risk !== Risk.HIGH && request.linesChanged <= 500) {
      return Decision.of(Approver.TEAM_LEAD, 'moderate change');
    } else if (request.risk !== Risk.HIGH || !request.production) {
      return Decision.of(Approver.RELEASE_MANAGER, 'large or non-production change');
    } else if (request.linesChanged <= 2_000) {
      return Decision.of(Approver.CHANGE_ADVISORY_BOARD, 'high-risk production change');
    } else {
      return Decision.of(Approver.REJECTED, 'no approver for ' + request.id);
    }
  }
}
