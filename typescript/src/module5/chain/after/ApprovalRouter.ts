import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ChangeRequest } from '../ChangeRequest.js';
import type { Decision } from '../Decision.js';
import type { ApprovalHandler } from './ApprovalHandler.js';
import { AutomaticApprovalHandler } from './AutomaticApprovalHandler.js';
import { ChangeAdvisoryBoardHandler } from './ChangeAdvisoryBoardHandler.js';
import { ReleaseManagerHandler } from './ReleaseManagerHandler.js';
import { SizeLimitHandler } from './SizeLimitHandler.js';
import { TeamLeadHandler } from './TeamLeadHandler.js';

/**
 * Miejsce, w którym łańcuch jest jawnie złożony. Kolejność ogniw jest widoczna
 * w jednym wyrażeniu i może być inna dla innego procesu zatwierdzania.
 */
export class ApprovalRouter {
  private readonly chain: ApprovalHandler;

  constructor(chain: ApprovalHandler) {
    this.chain = requireNonNull(chain, 'chain');
  }

  static standard(): ApprovalRouter {
    const first = new SizeLimitHandler();
    first
      .then(new AutomaticApprovalHandler())
      .then(new TeamLeadHandler())
      .then(new ReleaseManagerHandler())
      .then(new ChangeAdvisoryBoardHandler());
    return new ApprovalRouter(first);
  }

  route(request: ChangeRequest): Decision {
    return this.chain.handle(requireNonNull(request, 'request'));
  }
}
