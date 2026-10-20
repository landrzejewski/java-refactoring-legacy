import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

export class LegacyDeploymentRequest {
  readonly zoneCode: string;

  constructor(
    readonly releaseId: string,
    zoneCode: string,
  ) {
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }
    if (isBlank(zoneCode)) {
      throw new IllegalArgumentError('zoneCode must not be blank');
    }
    // toUpperCase() w JS nie zależy od locale (jak toUpperCase(Locale.ROOT)).
    const normalized = zoneCode.toUpperCase();
    if (normalized !== 'TEST' && normalized !== 'PROD' && normalized !== 'DR') {
      throw new IllegalArgumentError('unknown zone code: ' + normalized);
    }
    this.zoneCode = normalized;
  }

  requiresApproval(): boolean {
    return this.zoneCode === 'PROD' || this.zoneCode === 'DR';
  }
}
