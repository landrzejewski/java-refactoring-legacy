import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../../src/shared/errors.js';
import { NotificationFormatter } from '../../../src/module5/collapse/after/NotificationFormatter.js';
import { NotificationFormatter as BeforeNotificationFormatter } from '../../../src/module5/collapse/before/NotificationFormatter.js';

const NULL = null as unknown as string;

const notifications = [
  [
    'operational notification',
    'ops@example.com',
    'Deployment finished',
    'To: ops@example.com\nMessage: Deployment finished',
  ],
  [
    'Unicode content',
    'zespół@example.pl',
    'Zażółć gęślą jaźń',
    'To: zespół@example.pl\nMessage: Zażółć gęślą jaźń',
  ],
] as const;

describe('NotificationFormatterEquivalenceTest', () => {
  it('collapsedFormatterPreservesValidation', () => {
    const before = new BeforeNotificationFormatter();
    const after = new NotificationFormatter();

    expect.soft(() => before.format(NULL, 'message')).toThrow(NullPointerError);
    expect.soft(() => after.format(NULL, 'message')).toThrow(NullPointerError);
    expect.soft(() => before.format('recipient', NULL)).toThrow(NullPointerError);
    expect.soft(() => after.format('recipient', NULL)).toThrow(NullPointerError);
  });

  it.each(notifications)(
    'collapsedFormatterPreservesTheFormattedNotification: %s',
    (_scenario, recipient, message, expectedNotification) => {
      const before = new BeforeNotificationFormatter();
      const after = new NotificationFormatter();

      expect.soft(before.format(recipient, message)).toBe(expectedNotification);
      expect.soft(after.format(recipient, message)).toBe(expectedNotification);
      expect.soft(after.format(recipient, message)).toBe(before.format(recipient, message));
    },
  );
});
