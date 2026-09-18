import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

// Obiekt typu (Type Object) z kanonicznymi instancjami — prywatny konstruktor.
export class DeploymentZone {
  static readonly TEST = new DeploymentZone('TEST', false);
  static readonly PRODUCTION = new DeploymentZone('PROD', true);
  static readonly DISASTER_RECOVERY = new DeploymentZone('DR', true);

  private static readonly BY_CODE: ReadonlyMap<string, DeploymentZone> = new Map([
    [DeploymentZone.TEST.code, DeploymentZone.TEST],
    [DeploymentZone.PRODUCTION.code, DeploymentZone.PRODUCTION],
    [DeploymentZone.DISASTER_RECOVERY.code, DeploymentZone.DISASTER_RECOVERY],
  ]);

  private constructor(
    readonly code: string,
    private readonly approvalRequired: boolean,
  ) {}

  static fromCode(code: string): DeploymentZone {
    if (isBlank(code)) {
      throw new IllegalArgumentError('zoneCode must not be blank');
    }
    const normalized = code.toUpperCase();
    const zone = DeploymentZone.BY_CODE.get(normalized);
    if (zone === undefined) {
      throw new IllegalArgumentError('unknown zone code: ' + normalized);
    }
    return zone;
  }

  requiresApproval(): boolean {
    return this.approvalRequired;
  }

  toString(): string {
    return this.code;
  }
}
