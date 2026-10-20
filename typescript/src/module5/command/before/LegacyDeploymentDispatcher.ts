import { assertNever } from '../../../shared/assertNever.js';
import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';

// Odpowiednik zagnieżdżonego enuma LegacyDeploymentDispatcher.DeploymentAction.
export enum DeploymentAction {
  PAUSE = 'PAUSE',
  ROLLBACK = 'ROLLBACK',
}

export class LegacyDeploymentDispatcher {
  dispatch(action: DeploymentAction, releaseId: string): string {
    requireNonNull(action, 'action');
    if (isBlank(releaseId)) {
      throw new IllegalArgumentError('releaseId must not be blank');
    }

    switch (action) {
      case DeploymentAction.PAUSE:
        return 'paused:' + releaseId;
      case DeploymentAction.ROLLBACK:
        return 'rolled-back:' + releaseId;
      default:
        return assertNever(action);
    }
  }
}
