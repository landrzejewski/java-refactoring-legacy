import { assertNever } from '../../../shared/assertNever.js';
import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { DeploymentProbe } from './DeploymentProbe.js';

// Odpowiednik zagnieżdżonego enuma DeploymentProbeFactory.ProbeKind.
export enum ProbeKind {
  HTTP = 'HTTP',
  QUEUE = 'QUEUE',
}

export class DeploymentProbeFactory {
  create(kind: ProbeKind, target: string): DeploymentProbe {
    requireNonNull(kind, 'kind');
    if (isBlank(target)) {
      const field = kind === ProbeKind.HTTP ? 'endpoint' : 'queueName';
      throw new IllegalArgumentError(field + ' must not be blank');
    }

    switch (kind) {
      case ProbeKind.HTTP:
        return new HttpProbe(target);
      case ProbeKind.QUEUE:
        return new QueueProbe(target);
      default:
        return assertNever(kind);
    }
  }
}

// Konkretne klasy są prywatne dla modułu — klient zna tylko DeploymentProbe.
class HttpProbe implements DeploymentProbe {
  constructor(readonly endpoint: string) {}

  check(): string {
    return 'http-ok:' + this.endpoint;
  }
}

class QueueProbe implements DeploymentProbe {
  constructor(readonly queueName: string) {}

  check(): string {
    return 'queue-ok:' + this.queueName;
  }
}
