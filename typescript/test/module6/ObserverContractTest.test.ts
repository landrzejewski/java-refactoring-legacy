import { describe, expect, it } from 'vitest';
import { IllegalStateError } from '../../src/shared/errors.js';
import type { ReleaseListener } from '../../src/module6/observer/after/ReleaseListener.js';
import { ReleasePublished } from '../../src/module6/observer/after/ReleasePublished.js';
import { ReleasePublisher } from '../../src/module6/observer/after/ReleasePublisher.js';
import type { Subscription } from '../../src/module6/observer/after/Subscription.js';
import {
  LegacyReleasePublisher,
  PublishedRelease,
} from '../../src/module6/observer/before/LegacyReleasePublisher.js';

describe('ObserverContractTest', () => {
  it('preservesSynchronousRegistrationOrderForExistingRecipients', () => {
    const before: string[] = [];
    const after: string[] = [];
    const legacy = new LegacyReleasePublisher(
      event => before.push('audit:' + event.releaseId),
      event => before.push('metric:' + event.releaseId),
    );
    const publisher = new ReleasePublisher();
    publisher.subscribe(event => after.push('audit:' + event.releaseId));
    publisher.subscribe(event => after.push('metric:' + event.releaseId));

    legacy.publish(new PublishedRelease('rel-42'));
    publisher.publish(new ReleasePublished('rel-42'));

    expect(after).toEqual(before);
  });

  it('unsubscriptionDuringPublicationAffectsTheNextSnapshot', () => {
    const calls: string[] = [];
    const publisher = new ReleasePublisher();
    let later: Subscription | undefined;
    publisher.subscribe(event => {
      calls.push('first:' + event.releaseId);
      later?.close();
    });
    later = publisher.subscribe(event => calls.push('later:' + event.releaseId));

    publisher.publish(new ReleasePublished('one'));
    publisher.publish(new ReleasePublished('two'));

    expect(calls).toEqual(['first:one', 'later:one', 'first:two']);
  });

  it('usesFailFastExceptionPolicy', () => {
    const calls: string[] = [];
    const failure = new IllegalStateError('listener failed');
    const publisher = new ReleasePublisher();
    publisher.subscribe(() => {
      throw failure;
    });
    publisher.subscribe(() => calls.push('not-reached'));

    let propagated: unknown;
    try {
      publisher.publish(new ReleasePublished('rel-42'));
    } catch (error) {
      propagated = error;
    }

    expect(propagated).toBe(failure);
    expect(calls).toEqual([]);
  });

  it('duplicateRegistrationsRemainIndependentSubscriptions', () => {
    const publisher = new ReleasePublisher();
    const calls: string[] = [];
    const listener: ReleaseListener = () => calls.push('duplicate');
    const first = publisher.subscribe(listener);
    const middle = publisher.subscribe(() => calls.push('middle'));
    const second = publisher.subscribe(listener);

    second.close();
    publisher.publish(new ReleasePublished('one'));
    first.close();
    middle.close();
    publisher.publish(new ReleasePublished('two'));

    expect(calls).toEqual(['duplicate', 'middle']);
  });
});
