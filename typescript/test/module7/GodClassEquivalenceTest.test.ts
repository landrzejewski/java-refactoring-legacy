import { describe, expect, it } from 'vitest';
import { IllegalStateError } from '../../src/shared/errors.js';
import { AuditTrail } from '../../src/module7/godclass/after/AuditTrail.js';
import { InMemoryReleaseRepository } from '../../src/module7/godclass/after/InMemoryReleaseRepository.js';
import { ReleaseApplicationService } from '../../src/module7/godclass/after/ReleaseApplicationService.js';
import { ReleaseNotifier } from '../../src/module7/godclass/after/ReleaseNotifier.js';
import { ReleaseValidator } from '../../src/module7/godclass/after/ReleaseValidator.js';
import { LegacyReleaseManager } from '../../src/module7/godclass/before/LegacyReleaseManager.js';

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function assertNoAfterEffects(
  repository: InMemoryReleaseRepository,
  auditTrail: AuditTrail,
  notifier: ReleaseNotifier,
  events: readonly string[],
): void {
  expect(repository.findAll()).toEqual([]);
  expect(auditTrail.entries()).toEqual([]);
  expect(notifier.notifications()).toEqual([]);
  expect(events).toEqual([]);
}

function assertSameFailure(before: Error, after: Error): void {
  expect(after.constructor).toBe(before.constructor);
  expect(after.message).toBe(before.message);
}

// Odpowiednik list.clear() na List.copyOf: zamrożona tablica w trybie
// strict (ESM) rzuca TypeError przy próbie modyfikacji.
function clear(list: readonly unknown[]): void {
  (list as unknown[]).length = 0;
}

describe('GodClassEquivalenceTest', () => {
  it('collaboratorsPreserveResultEffectsAndTheirOrder', () => {
    const before = new LegacyReleaseManager();
    const events: string[] = [];
    const record = (event: string): void => { events.push(event); };
    const repository = new InMemoryReleaseRepository(record);
    const auditTrail = new AuditTrail(record);
    const notifier = new ReleaseNotifier(record);
    const after = new ReleaseApplicationService(
      new ReleaseValidator(), repository, auditTrail, notifier);

    const legacyResult = before.publish('rel-42', 'payments', '2.1.0');
    const refactoredResult = after.publish('rel-42', 'payments', '2.1.0');

    expect(refactoredResult).toEqual(legacyResult);
    expect(repository.findAll()).toEqual(before.releases());
    expect(auditTrail.entries()).toEqual(before.auditEntries());
    expect(notifier.notifications()).toEqual(before.notifications());
    expect(events).toEqual(['save:rel-42', 'audit:rel-42', 'notify:rel-42']);
    expect(events).toEqual(before.events());
  });

  it('validationAndDuplicateFailuresDoNotCreateFurtherEffects', () => {
    const before = new LegacyReleaseManager();
    const events: string[] = [];
    const record = (event: string): void => { events.push(event); };
    const repository = new InMemoryReleaseRepository(record);
    const auditTrail = new AuditTrail(record);
    const notifier = new ReleaseNotifier(record);
    const after = new ReleaseApplicationService(
      new ReleaseValidator(), repository, auditTrail, notifier);

    const legacyValidationFailure = failureOf(
      () => before.publish(' ', 'payments', '2.1.0'));
    const refactoredValidationFailure = failureOf(
      () => after.publish(' ', 'payments', '2.1.0'));
    assertSameFailure(legacyValidationFailure, refactoredValidationFailure);
    assertNoAfterEffects(repository, auditTrail, notifier, events);
    expect(before.events()).toEqual([]);

    before.publish('rel-42', 'payments', '2.1.0');
    after.publish('rel-42', 'payments', '2.1.0');
    const eventsBeforeDuplicate = [...events];
    const legacyDuplicateFailure = failureOf(
      () => before.publish('rel-42', 'other', '9.0.0'));
    const refactoredDuplicateFailure = failureOf(
      () => after.publish('rel-42', 'other', '9.0.0'));

    assertSameFailure(legacyDuplicateFailure, refactoredDuplicateFailure);
    expect(events).toEqual(eventsBeforeDuplicate);
    expect(repository.findAll().length).toBe(1);
    expect(auditTrail.entries().length).toBe(1);
    expect(notifier.notifications().length).toBe(1);
    expect(repository.findAll()).toEqual(before.releases());
    expect(auditTrail.entries()).toEqual(before.auditEntries());
    expect(notifier.notifications()).toEqual(before.notifications());
  });

  it('exposedCollectionsAreDefensiveSnapshots', () => {
    const before = new LegacyReleaseManager();
    before.publish('rel-42', 'payments', '2.1.0');
    const repository = new InMemoryReleaseRepository();
    const auditTrail = new AuditTrail();
    const notifier = new ReleaseNotifier();
    const after = new ReleaseApplicationService(
      new ReleaseValidator(), repository, auditTrail, notifier);
    after.publish('rel-42', 'payments', '2.1.0');

    expect(() => clear(before.releases())).toThrow(TypeError);
    expect(() => clear(before.auditEntries())).toThrow(TypeError);
    expect(() => clear(before.notifications())).toThrow(TypeError);
    expect(() => clear(repository.findAll())).toThrow(TypeError);
    expect(() => clear(auditTrail.entries())).toThrow(TypeError);
    expect(() => clear(notifier.notifications())).toThrow(TypeError);
  });

  it('repositoryFailureStopsLaterEffectsAndKeepsTheCompletedSave', () => {
    const events: string[] = [];
    const repository = new InMemoryReleaseRepository(event => {
      events.push(event);
      throw new IllegalStateError('repository event failure');
    });
    const auditTrail = new AuditTrail(event => { events.push(event); });
    const notifier = new ReleaseNotifier(event => { events.push(event); });
    const service = new ReleaseApplicationService(
      new ReleaseValidator(), repository, auditTrail, notifier);

    const failure = failureOf(() => service.publish('rel-42', 'payments', '2.1.0'));

    expect(failure).toBeInstanceOf(IllegalStateError);
    expect(failure.message).toBe('repository event failure');
    expect(events).toEqual(['save:rel-42']);
    expect(repository.findAll().length).toBe(1);
    expect(auditTrail.entries()).toEqual([]);
    expect(notifier.notifications()).toEqual([]);
  });

  it('auditFailureStopsNotificationAndKeepsEarlierEffects', () => {
    const events: string[] = [];
    const repository = new InMemoryReleaseRepository(event => { events.push(event); });
    const auditTrail = new AuditTrail(event => {
      events.push(event);
      throw new IllegalStateError('audit event failure');
    });
    const notifier = new ReleaseNotifier(event => { events.push(event); });
    const service = new ReleaseApplicationService(
      new ReleaseValidator(), repository, auditTrail, notifier);

    const failure = failureOf(() => service.publish('rel-42', 'payments', '2.1.0'));

    expect(failure).toBeInstanceOf(IllegalStateError);
    expect(failure.message).toBe('audit event failure');
    expect(events).toEqual(['save:rel-42', 'audit:rel-42']);
    expect(repository.findAll().length).toBe(1);
    expect(auditTrail.entries()).toEqual(['published:rel-42']);
    expect(notifier.notifications()).toEqual([]);
  });

  it('notificationFailureKeepsAllEarlierEffects', () => {
    const events: string[] = [];
    const repository = new InMemoryReleaseRepository(event => { events.push(event); });
    const auditTrail = new AuditTrail(event => { events.push(event); });
    const notifier = new ReleaseNotifier(event => {
      events.push(event);
      throw new IllegalStateError('notification event failure');
    });
    const service = new ReleaseApplicationService(
      new ReleaseValidator(), repository, auditTrail, notifier);

    const failure = failureOf(() => service.publish('rel-42', 'payments', '2.1.0'));

    expect(failure).toBeInstanceOf(IllegalStateError);
    expect(failure.message).toBe('notification event failure');
    expect(events).toEqual(['save:rel-42', 'audit:rel-42', 'notify:rel-42']);
    expect(repository.findAll().length).toBe(1);
    expect(auditTrail.entries()).toEqual(['published:rel-42']);
    expect(notifier.notifications()).toEqual(['release-published:rel-42']);
  });
});
