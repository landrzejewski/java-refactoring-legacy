import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentProbeFactory, ProbeKind } from './DeploymentProbeFactory.js';

export class ProbeService {
  private readonly factory: DeploymentProbeFactory;

  constructor(factory: DeploymentProbeFactory) {
    this.factory = requireNonNull(factory, 'factory');
  }

  check(kind: ProbeKind, target: string): string {
    return this.factory.create(kind, target).check();
  }
}
