import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../../src/shared/errors.js';
import { EmailNotification } from '../../../src/module5/stage3/EmailNotification.js';
import { NotificationBatch } from '../../../src/module5/stage3/NotificationBatch.js';
import type { OutboundNotification } from '../../../src/module5/stage3/OutboundNotification.js';
import { SmsNotification } from '../../../src/module5/stage3/SmsNotification.js';

describe('OutboundNotificationContractTest', () => {
  it('everyImplementationSatisfiesTheExtractedContract', () => {
    const notifications: readonly OutboundNotification[] = [
      new EmailNotification('e-1', 'ops', 'Ready', false),
      new SmsNotification('s-1', 'ops', 'Ready', true),
    ];

    expect(notifications.map((notification) => notification.dispatch(true))).toEqual([
      'e-1|OPS|Ready|EMAIL|SENT',
      's-1|OPS|Ready|SMS|SENT|RECEIPT',
    ]);
  });

  it('batchDependsOnlyOnTheClientRole', () => {
    const batch = new NotificationBatch();
    const notifications: readonly OutboundNotification[] = [
      new EmailNotification('e-1', 'ops', 'Ready'),
      new SmsNotification('s-1', 'ops', 'Ready', false),
    ];

    const results = batch.dispatchAll(notifications, false);

    expect(results).toEqual(['e-1|OPS|Ready|EMAIL|FAILED', 's-1|OPS|Ready|SMS|FAILED']);
    expect(() => (results as string[]).push('unexpected')).toThrow(TypeError);
  });

  it('batchRejectsInvalidInputs', () => {
    const batch = new NotificationBatch();
    let dispatchCalls = 0;
    // Odpowiednik lambdy implementującej interfejs funkcyjny.
    const recordingNotification: OutboundNotification = {
      dispatch: () => {
        dispatchCalls++;
        return 'dispatched';
      },
    };

    expect(() => batch.dispatchAll(null as unknown as OutboundNotification[], true))
      .toThrow(NullPointerError);
    expect(() => batch.dispatchAll(
      [recordingNotification, null as unknown as OutboundNotification],
      true,
    )).toThrow(NullPointerError);
    expect(dispatchCalls).toBe(0);
  });
});
