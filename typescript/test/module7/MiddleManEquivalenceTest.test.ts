import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../src/shared/errors.js';
import { DeploymentStatus } from '../../src/module7/middleman/DeploymentStatus.js';
import * as after from '../../src/module7/middleman/after/DeploymentRegistry.js';
import * as afterDashboardModule from '../../src/module7/middleman/after/ReleaseDashboard.js';
import * as before from '../../src/module7/middleman/before/DeploymentRegistry.js';
import * as beforeDashboardModule from '../../src/module7/middleman/before/ReleaseDashboard.js';
import { ReleaseService } from '../../src/module7/middleman/before/ReleaseService.js';

const NULL = null as never;

function messageOf(errorType: new (message?: string) => Error, action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(errorType);
    return (error as Error).message;
  }
  throw new Error('Expected action to throw');
}

function fixture() {
  const beforeRegistry = new before.DeploymentRegistry();
  const beforeService = new ReleaseService(beforeRegistry);
  const beforeDashboard = new beforeDashboardModule.ReleaseDashboard(beforeService);

  const afterRegistry = new after.DeploymentRegistry();
  const afterDashboard = new afterDashboardModule.ReleaseDashboard(afterRegistry);

  return { beforeService, beforeDashboard, afterRegistry, afterDashboard };
}

describe('MiddleManEquivalenceTest', () => {
  it('directCollaborationPreservesLookupUpdatesOverwriteAndRendering', () => {
    const { beforeService, beforeDashboard, afterRegistry, afterDashboard } = fixture();

    expect(afterRegistry.statusOf('dep-42')).toBe(beforeService.statusOf('dep-42'));
    expect(afterRegistry.statusOf('dep-42')).toBe(DeploymentStatus.UNKNOWN);
    expect(afterDashboard.render('dep-42')).toBe(beforeDashboard.render('dep-42'));

    beforeService.update('dep-42', DeploymentStatus.RUNNING);
    afterRegistry.update('dep-42', DeploymentStatus.RUNNING);
    expect(afterRegistry.statusOf('dep-42')).toBe(beforeService.statusOf('dep-42'));
    expect(afterDashboard.render('dep-42')).toBe(beforeDashboard.render('dep-42'));

    beforeService.update('dep-42', DeploymentStatus.SUCCEEDED);
    afterRegistry.update('dep-42', DeploymentStatus.SUCCEEDED);
    expect(afterRegistry.statusOf('dep-42')).toBe(beforeService.statusOf('dep-42'));
    expect(afterDashboard.render('dep-42')).toBe('dep-42 -> SUCCEEDED');
    expect(afterDashboard.render('dep-42')).toBe(beforeDashboard.render('dep-42'));
  });

  it('validationRemainsAtTheRegistryBoundary', () => {
    const { beforeService, beforeDashboard, afterRegistry, afterDashboard } = fixture();

    for (const invalidId of [NULL as string, '', '  \t']) {
      expect(messageOf(IllegalArgumentError, () => afterRegistry.statusOf(invalidId)))
        .toBe(messageOf(IllegalArgumentError, () => beforeService.statusOf(invalidId)));
      expect(messageOf(IllegalArgumentError, () => afterDashboard.render(invalidId)))
        .toBe(messageOf(IllegalArgumentError, () => beforeDashboard.render(invalidId)));
    }

    expect(messageOf(NullPointerError, () => afterRegistry.update('dep-42', NULL)))
      .toBe(messageOf(NullPointerError, () => beforeService.update('dep-42', NULL)));
    expect(beforeService.statusOf('dep-42')).toBe(DeploymentStatus.UNKNOWN);
    expect(afterRegistry.statusOf('dep-42')).toBe(DeploymentStatus.UNKNOWN);
  });
});
