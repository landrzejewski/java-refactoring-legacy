import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';


// Etap 2: Push Down Field/Method - deliveryReceipt i appendReceipt tylko w SmsNotification.
// W Javie klasa jest pakietowa, a metody final; w TS "final" pozostaje konwencją.
export abstract class Notification {
  readonly #messageId: string;
  readonly #senderId: string;
  readonly #body: string;

  protected constructor(messageId: string, senderId: string, body: string) {
    this.#messageId = Notification.normalized(messageId, 'messageId');
    this.#senderId = Notification.normalized(senderId, 'senderId').toUpperCase();
    this.#body = Notification.normalized(body, 'body');
  }

  /* final */ messageId(): string {
    return this.#messageId;
  }

  /* final */ summary(): string {
    return this.#messageId + '|' + this.#senderId + '|' + this.#body + '|' + this.channel();
  }

  protected /* final */ dispatchResult(successful: boolean): string {
    return this.summary() + (successful ? '|SENT' : '|FAILED');
  }

  protected abstract channel(): string;

  abstract dispatch(successful: boolean): string;

  private static normalized(value: string, fieldName: string): string {
    const normalized = requireNonNull(value, `${fieldName} must not be null`).trim();
    if (normalized.length === 0) {
      throw new IllegalArgumentError(`${fieldName} must not be blank`);
    }
    return normalized;
  }
}
