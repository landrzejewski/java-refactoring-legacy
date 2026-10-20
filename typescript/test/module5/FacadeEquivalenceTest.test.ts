import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { ReleaseCli } from '../../src/module5/facade/after/ReleaseCli.js';
import * as facadeModule from '../../src/module5/facade/after/ReleasePublishingFacade.js';
import { ReleasePublishingFacade } from '../../src/module5/facade/after/ReleasePublishingFacade.js';
import { ReleaseScheduler } from '../../src/module5/facade/after/ReleaseScheduler.js';
import { ArtifactStore } from '../../src/module5/facade/before/ArtifactStore.js';
import { ConfigRenderer } from '../../src/module5/facade/before/ConfigRenderer.js';
import { DeploymentQueue } from '../../src/module5/facade/before/DeploymentQueue.js';
import { LegacyReleaseCli } from '../../src/module5/facade/before/LegacyReleaseCli.js';
import { LegacyReleaseScheduler } from '../../src/module5/facade/before/LegacyReleaseScheduler.js';
import { ReleaseNotifier } from '../../src/module5/facade/before/ReleaseNotifier.js';

function failureMessage(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalArgumentError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalArgumentError');
}

function legacyCli(effects: string[]): LegacyReleaseCli {
  const record = (effect: string): void => {
    effects.push(effect);
  };
  return new LegacyReleaseCli(
    new ArtifactStore(record),
    new ConfigRenderer(record),
    new DeploymentQueue(record),
    new ReleaseNotifier(record),
  );
}

describe('FacadeEquivalenceTest', () => {
  it('cliProducesTheSameEffectsInTheSameOrder', () => {
    const before: string[] = [];
    const after: string[] = [];
    const legacy = legacyCli(before);
    const cli = new ReleaseCli(new ReleasePublishingFacade(effect => after.push(effect)));

    expect(cli.publish('rel-42', ' PROD ')).toBe(legacy.publish('rel-42', ' PROD '));
    expect(after).toEqual([
      'stored rel-42.jar',
      'rendered rel-42/prod.yaml',
      'queued rel-42.jar with rel-42/prod.yaml',
      'announced rel-42 on prod',
    ]);
    expect(after).toEqual(before);
  });

  it('schedulerProducesTheSameEffectsInTheSameOrder', () => {
    const before: string[] = [];
    const after: string[] = [];
    const record = (effect: string): void => {
      before.push(effect);
    };
    const legacyScheduler = new LegacyReleaseScheduler(
      new ArtifactStore(record),
      new ConfigRenderer(record),
      new DeploymentQueue(record),
      new ReleaseNotifier(record),
    );
    const scheduler = new ReleaseScheduler(new ReleasePublishingFacade(effect => after.push(effect)));

    expect(scheduler.publishNightly(['rel-1', 'rel-2'])).toEqual(legacyScheduler.publishNightly(['rel-1', 'rel-2']));
    expect(after).toEqual(before);
  });

  it('invalidRequestsAreRejectedBeforeAnySideEffect', () => {
    const before: string[] = [];
    const after: string[] = [];
    const legacy = legacyCli(before);
    const facade = new ReleasePublishingFacade(effect => after.push(effect));

    for (const [releaseId, environment] of [['rel-1', 'qa'], [' ', 'prod']] as const) {
      expect(failureMessage(() => facade.publish(releaseId, environment))).toBe(
        failureMessage(() => legacy.publish(releaseId, environment)),
      );
    }
    expect(before).toEqual([]);
    expect(after).toEqual([]);
  });

  it('subsystemsAreHiddenBehindTheFacade', () => {
    // Java sprawdza refleksją, że podsystemy nie są publiczne. TS nie ma widoczności
    // pakietowej: podsystemy są klasami prywatnymi modułu fasady, więc sprawdzamy jego eksporty.
    expect(Object.keys(facadeModule)).toEqual(['ReleasePublishingFacade']);
    for (const name of ['ArtifactStore', 'ConfigRenderer', 'DeploymentQueue', 'ReleaseNotifier']) {
      expect(name in facadeModule, name).toBe(false);
    }
  });
});
