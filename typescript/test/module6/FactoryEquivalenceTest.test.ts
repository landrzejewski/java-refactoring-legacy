import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { DeploymentProbeFactory, ProbeKind } from '../../src/module6/factory/after/DeploymentProbeFactory.js';
import { ProbeService } from '../../src/module6/factory/after/ProbeService.js';
import { HttpProbe } from '../../src/module6/factory/before/HttpProbe.js';
import {
  LegacyProbeService,
  ProbeKind as LegacyProbeKind,
} from '../../src/module6/factory/before/LegacyProbeService.js';
import { QueueProbe } from '../../src/module6/factory/before/QueueProbe.js';

function failureMessage(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalArgumentError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalArgumentError');
}

describe('FactoryEquivalenceTest', () => {
  it('factoryHidesConcreteClassesWithoutChangingBehavior', () => {
    const factory = new DeploymentProbeFactory();

    expect(factory.create(ProbeKind.HTTP, '/health').check()).toBe(new HttpProbe('/health').check());
    expect(factory.create(ProbeKind.QUEUE, 'deployments').check()).toBe(
      new QueueProbe('deployments').check(),
    );
  });

  it('rejectsAnInvalidTargetAtTheCreationBoundary', () => {
    const before = new LegacyProbeService();
    const after = new ProbeService(new DeploymentProbeFactory());

    expect(failureMessage(() => after.check(ProbeKind.HTTP, ' '))).toBe(
      failureMessage(() => before.check(LegacyProbeKind.HTTP, ' ')),
    );
    expect(failureMessage(() => after.check(ProbeKind.QUEUE, ' '))).toBe(
      failureMessage(() => before.check(LegacyProbeKind.QUEUE, ' ')),
    );
  });

  it('factoryPreservesFreshInstanceSemantics', () => {
    const factory = new DeploymentProbeFactory();

    expect(factory.create(ProbeKind.HTTP, '/health')).not.toBe(factory.create(ProbeKind.HTTP, '/health'));
  });

  it('extractedFactoryRemovesCreationKnowledgeFromTheService', () => {
    const before = new LegacyProbeService();
    const after = new ProbeService(new DeploymentProbeFactory());

    expect(after.check(ProbeKind.HTTP, '/health')).toBe(before.check(LegacyProbeKind.HTTP, '/health'));
    expect(after.check(ProbeKind.QUEUE, 'deployments')).toBe(
      before.check(LegacyProbeKind.QUEUE, 'deployments'),
    );
  });
});
