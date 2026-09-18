import { assertNever } from '../../../shared/assertNever.js';
import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';

// Odpowiednik zagnieżdżonego enuma LegacyDeploymentStep.Kind.
export enum Kind {
  SCRIPT = 'SCRIPT',
  APPROVAL = 'APPROVAL',
}

export class LegacyDeploymentStep {
  constructor(
    readonly kind: Kind,
    readonly value: string,
  ) {
    requireNonNull(kind, 'kind');
    if (isBlank(value)) {
      const field = kind === Kind.SCRIPT ? 'command' : 'approver';
      throw new IllegalArgumentError(field + ' must not be blank');
    }
  }

  static script(command: string): LegacyDeploymentStep {
    return new LegacyDeploymentStep(Kind.SCRIPT, command);
  }

  static approval(approver: string): LegacyDeploymentStep {
    return new LegacyDeploymentStep(Kind.APPROVAL, approver);
  }

  execute(): string {
    switch (this.kind) {
      case Kind.SCRIPT:
        return 'executed:' + this.value;
      case Kind.APPROVAL:
        return 'approved-by:' + this.value;
      default:
        return assertNever(this.kind);
    }
  }
}
