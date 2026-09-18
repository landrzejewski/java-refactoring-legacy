import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { DeploymentAction } from '../../src/module6/command/after/DeploymentAction.js';
import type { DeploymentCommand } from '../../src/module6/command/after/DeploymentCommand.js';
import { DeploymentCommandDispatcher } from '../../src/module6/command/after/DeploymentCommandDispatcher.js';
import { PauseDeployment } from '../../src/module6/command/after/PauseDeployment.js';
import { RollbackDeployment } from '../../src/module6/command/after/RollbackDeployment.js';
import {
  DeploymentAction as LegacyDeploymentAction,
  LegacyDeploymentDispatcher,
} from '../../src/module6/command/before/LegacyDeploymentDispatcher.js';

describe('CommandEquivalenceTest', () => {
  it('lookupPreservesEveryLegacyDispatchBranch', () => {
    const before = new LegacyDeploymentDispatcher();
    const after = new DeploymentCommandDispatcher(
      new Map<DeploymentAction, DeploymentCommand>([
        [DeploymentAction.PAUSE, new PauseDeployment()],
        [DeploymentAction.ROLLBACK, new RollbackDeployment()],
      ]),
    );

    expect(after.dispatch(DeploymentAction.PAUSE, 'rel-42')).toBe(
      before.dispatch(LegacyDeploymentAction.PAUSE, 'rel-42'),
    );
    expect(after.dispatch(DeploymentAction.ROLLBACK, 'rel-42')).toBe(
      before.dispatch(LegacyDeploymentAction.ROLLBACK, 'rel-42'),
    );
  });

  it('registryIsDefensivelyCopiedAndMustBeComplete', () => {
    const source = new Map<DeploymentAction, DeploymentCommand>();
    source.set(DeploymentAction.PAUSE, new PauseDeployment());
    source.set(DeploymentAction.ROLLBACK, new RollbackDeployment());
    const dispatcher = new DeploymentCommandDispatcher(source);
    source.set(DeploymentAction.PAUSE, { execute: () => 'changed' });
    source.delete(DeploymentAction.ROLLBACK);

    expect(dispatcher.dispatch(DeploymentAction.PAUSE, 'rel-42')).toBe('paused:rel-42');
    expect(dispatcher.dispatch(DeploymentAction.ROLLBACK, 'rel-42')).toBe('rolled-back:rel-42');
    const incomplete = () =>
      new DeploymentCommandDispatcher(new Map([[DeploymentAction.PAUSE, new PauseDeployment()]]));
    expect(incomplete).toThrow(IllegalArgumentError);
    expect(incomplete).toThrow('missing commands: [ROLLBACK]');
  });
});
