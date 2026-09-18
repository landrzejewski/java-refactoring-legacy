import { assertNever } from '../../../shared/assertNever.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentProbe } from './DeploymentProbe.js';
import { HttpProbe } from './HttpProbe.js';
import { QueueProbe } from './QueueProbe.js';

// Odpowiednik zagnieżdżonego enuma LegacyProbeService.ProbeKind.
export enum ProbeKind {
  HTTP = 'HTTP',
  QUEUE = 'QUEUE',
}

export class LegacyProbeService {
  check(kind: ProbeKind, target: string): string {
    const probe = createProbe(requireNonNull(kind, 'kind'), target);
    return probe.check();
  }
}

function createProbe(kind: ProbeKind, target: string): DeploymentProbe {
  switch (kind) {
    case ProbeKind.HTTP:
      return new HttpProbe(target);
    case ProbeKind.QUEUE:
      return new QueueProbe(target);
    default:
      return assertNever(kind);
  }
}
