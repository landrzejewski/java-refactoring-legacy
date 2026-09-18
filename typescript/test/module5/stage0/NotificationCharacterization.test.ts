import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../../src/shared/errors.js';
import { EmailNotification } from '../../../src/module5/stage0/EmailNotification.js';
import { SmsNotification } from '../../../src/module5/stage0/SmsNotification.js';

describe('NotificationCharacterizationTest', () => {
  it('capturesEmailBehavior', () => {
    const notification = new EmailNotification(' msg-1 ', ' ops ', ' Deployment ready ', true);

    expect(notification.messageId()).toBe('msg-1');
    expect(notification.summary()).toBe('msg-1|OPS|Deployment ready|EMAIL');
    expect(notification.dispatch(true)).toBe('msg-1|OPS|Deployment ready|EMAIL|SENT');
    expect(notification.dispatch(false)).toBe('msg-1|OPS|Deployment ready|EMAIL|FAILED');
  });

  it('capturesSmsReceiptBehavior', () => {
    const withReceipt = new SmsNotification('msg-2', 'ops', 'Deploy now', true);
    const withoutReceipt = new SmsNotification('msg-2', 'ops', 'Deploy now', false);

    expect(withReceipt.dispatch(true)).toBe('msg-2|OPS|Deploy now|SMS|SENT|RECEIPT');
    expect(withReceipt.dispatch(false)).toBe('msg-2|OPS|Deploy now|SMS|FAILED');
    expect(withoutReceipt.dispatch(true)).toBe('msg-2|OPS|Deploy now|SMS|SENT');
  });

  it('capturesValidation', () => {
    expect(() => new EmailNotification(' ', 'ops', 'body', false)).toThrow(IllegalArgumentError);
    expect(() => new SmsNotification('msg', null as unknown as string, 'body', false))
      .toThrow(NullPointerError);
  });
});
