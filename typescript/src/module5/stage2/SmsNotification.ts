import { Notification } from './Notification.js';

export class SmsNotification extends Notification {
  readonly #deliveryReceipt: boolean;

  constructor(messageId: string, senderId: string, body: string, deliveryReceipt: boolean) {
    super(messageId, senderId, body);
    this.#deliveryReceipt = deliveryReceipt;
  }

  protected override channel(): string {
    return 'SMS';
  }

  override dispatch(successful: boolean): string {
    const result = this.dispatchResult(successful);
    return successful ? this.appendReceipt(result) : result;
  }

  private appendReceipt(result: string): string {
    return this.#deliveryReceipt ? result + '|RECEIPT' : result;
  }
}
