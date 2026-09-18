import { describe, expect, it } from 'vitest';
import { DeliveryJob } from '../../../src/module5/extractsubclass/after/DeliveryJob.js';
import { DeliveryJob as BeforeDeliveryJob } from '../../../src/module5/extractsubclass/before/DeliveryJob.js';

const SCHEDULED_AT = new Date('2030-06-15T10:15:30Z');

const plusSeconds = (instant: Date, seconds: number): Date =>
  new Date(instant.getTime() + seconds * 1000);

const scheduledDispatchScenarios = [
  ['before the scheduled time', plusSeconds(SCHEDULED_AT, -1), 'WAITING_UNTIL 2030-06-15T10:15:30Z'],
  ['at the scheduled time', SCHEDULED_AT, 'SENT'],
  ['after the scheduled time', plusSeconds(SCHEDULED_AT, 1), 'SENT'],
] as const;

describe('DeliveryJobEquivalenceTest', () => {
  it('immediateJobIsSentAtTheRequestedTime', () => {
    const now = new Date('2029-01-01T00:00:00Z');
    const before = BeforeDeliveryJob.immediate();
    const after = DeliveryJob.immediate();

    expect.soft(before.dispatchAt(now)).toBe('SENT');
    expect.soft(after.dispatchAt(now)).toBe('SENT');
  });

  it.each(scheduledDispatchScenarios)(
    'scheduledJobPreservesItsObservableResult: %s',
    (_scenario, now, expectedResult) => {
      const before = BeforeDeliveryJob.scheduled(SCHEDULED_AT);
      const after = DeliveryJob.scheduled(SCHEDULED_AT);

      expect.soft(before.dispatchAt(now)).toBe(expectedResult);
      expect.soft(after.dispatchAt(now)).toBe(expectedResult);
      expect.soft(after.dispatchAt(now)).toBe(before.dispatchAt(now));
    },
  );
});
