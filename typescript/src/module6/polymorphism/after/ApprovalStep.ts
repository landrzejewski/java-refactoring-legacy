import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

export class ApprovalStep {
  readonly kind = 'approval';

  constructor(readonly approver: string) {
    if (isBlank(approver)) {
      throw new IllegalArgumentError('approver must not be blank');
    }
  }

  execute(): string {
    return 'approved-by:' + this.approver;
  }
}
