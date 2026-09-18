import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../src/shared/errors.js';
import { EmailNotification as Stage0Email } from '../../src/module5/stage0/EmailNotification.js';
import { SmsNotification as Stage0Sms } from '../../src/module5/stage0/SmsNotification.js';
import { EmailNotification as Stage1Email } from '../../src/module5/stage1/EmailNotification.js';
import { SmsNotification as Stage1Sms } from '../../src/module5/stage1/SmsNotification.js';
import { EmailNotification as Stage2Email } from '../../src/module5/stage2/EmailNotification.js';
import { SmsNotification as Stage2Sms } from '../../src/module5/stage2/SmsNotification.js';
import { EmailNotification as Stage3Email } from '../../src/module5/stage3/EmailNotification.js';
import { SmsNotification as Stage3Sms } from '../../src/module5/stage3/SmsNotification.js';

const NULL = null as unknown as string;

function assertAllEqual(expected: string, results: readonly string[]): void {
  results.forEach((result) => expect(result).toBe(expected));
}

// assertThrows + porównanie klasy: dokładnie ten typ, nie podklasa.
function assertExceptionType(
  expectedType: new (...args: never[]) => Error,
  scenarios: readonly (() => unknown)[],
): void {
  scenarios.forEach((scenario) => {
    let thrown: unknown;
    try {
      scenario();
    } catch (error) {
      thrown = error;
    }
    expect(thrown).toBeInstanceOf(expectedType);
    expect((thrown as Error).constructor).toBe(expectedType);
  });
}

describe('NotificationHierarchyEquivalenceTest', () => {
  it('allStagesPreserveEmailContractForEveryLegacyFlagAndStatus', () => {
    for (const deliveryReceipt of [false, true]) {
      for (const successful of [false, true]) {
        const results = [
          new Stage0Email(' msg-1 ', ' ops ', ' Deployment ready ', deliveryReceipt).dispatch(successful),
          new Stage1Email(' msg-1 ', ' ops ', ' Deployment ready ', deliveryReceipt).dispatch(successful),
          new Stage2Email(' msg-1 ', ' ops ', ' Deployment ready ', deliveryReceipt).dispatch(successful),
          new Stage3Email(' msg-1 ', ' ops ', ' Deployment ready ', deliveryReceipt).dispatch(successful),
        ];
        const expected = 'msg-1|OPS|Deployment ready|EMAIL|' + (successful ? 'SENT' : 'FAILED');

        assertAllEqual(expected, results);
      }
    }
  });

  it('allStagesPreserveSmsContractForEveryReceiptAndStatus', () => {
    for (const deliveryReceipt of [false, true]) {
      for (const successful of [false, true]) {
        const results = [
          new Stage0Sms('msg-2', 'ops', 'Deploy now', deliveryReceipt).dispatch(successful),
          new Stage1Sms('msg-2', 'ops', 'Deploy now', deliveryReceipt).dispatch(successful),
          new Stage2Sms('msg-2', 'ops', 'Deploy now', deliveryReceipt).dispatch(successful),
          new Stage3Sms('msg-2', 'ops', 'Deploy now', deliveryReceipt).dispatch(successful),
        ];
        const expected = 'msg-2|OPS|Deploy now|SMS|'
          + (successful ? 'SENT' : 'FAILED')
          + (successful && deliveryReceipt ? '|RECEIPT' : '');

        assertAllEqual(expected, results);
      }
    }
  });

  it('allStagesPreserveValidationTypes', () => {
    assertExceptionType(IllegalArgumentError, [
      () => new Stage0Email(' ', 'ops', 'body', false),
      () => new Stage1Email(' ', 'ops', 'body', false),
      () => new Stage2Email(' ', 'ops', 'body', false),
      () => new Stage3Email(' ', 'ops', 'body', false),
    ]);
    assertExceptionType(NullPointerError, [
      () => new Stage0Sms('msg', NULL, 'body', false),
      () => new Stage1Sms('msg', NULL, 'body', false),
      () => new Stage2Sms('msg', NULL, 'body', false),
      () => new Stage3Sms('msg', NULL, 'body', false),
    ]);
  });
});
