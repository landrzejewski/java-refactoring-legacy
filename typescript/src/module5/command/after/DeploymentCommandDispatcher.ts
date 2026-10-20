import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { formatList, isBlank } from '../../support.js';
import { DeploymentAction } from './DeploymentAction.js';
import type { DeploymentCommand } from './DeploymentCommand.js';

export class DeploymentCommandDispatcher {
  private readonly commands: ReadonlyMap<DeploymentAction, DeploymentCommand>;

  constructor(commands: ReadonlyMap<DeploymentAction, DeploymentCommand>) {
    // Kopia obronna (Map.copyOf) — późniejsze zmiany źródła nie wpływają na dispatcher.
    const copy = new Map(requireNonNull(commands, 'commands'));
    // Kolejność jak w EnumSet.allOf (kolejność deklaracji enuma).
    const missing = Object.values(DeploymentAction).filter(action => !copy.has(action));
    if (missing.length > 0) {
      throw new IllegalArgumentError('missing commands: ' + formatList(missing));
    }
    this.commands = copy;
  }

  dispatch(action: DeploymentAction, releaseId: string): string {
    requireNonNull(action, 'action');
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }

    return requireNonNull(this.commands.get(action)).execute(releaseId);
  }
}
