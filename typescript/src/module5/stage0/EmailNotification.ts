import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

// Stan wyjściowy: Email i Sms to niemal identyczne kopie (zduplikowane pola i walidacja).
export class EmailNotification {
  readonly #messageId: string;
  readonly #senderId: string;
  readonly #body: string;
  readonly #deliveryReceipt: boolean;

  constructor(messageId: string, senderId: string, body: string, deliveryReceipt: boolean) {
    this.#messageId = EmailNotification.normalized(messageId, 'messageId');
    this.#senderId = EmailNotification.normalized(senderId, 'senderId').toUpperCase();
    this.#body = EmailNotification.normalized(body, 'body');
    this.#deliveryReceipt = deliveryReceipt;
  }

  messageId(): string {
    return this.#messageId;
  }

  summary(): string {
    return this.#messageId + '|' + this.#senderId + '|' + this.#body + '|EMAIL';
  }

  dispatch(successful: boolean): string {
    return this.summary() + (successful ? '|SENT' : '|FAILED');
  }

  // @SuppressWarnings("unused") - martwy kod skopiowany z SmsNotification.
  private appendReceipt(result: string): string {
    return this.#deliveryReceipt ? result + '|RECEIPT' : result;
  }

  private static normalized(value: string, fieldName: string): string {
    const normalized = requireNonNull(value, `${fieldName} must not be null`).trim();
    if (normalized.length === 0) {
      throw new IllegalArgumentError(`${fieldName} must not be blank`);
    }
    return normalized;
  }
}
