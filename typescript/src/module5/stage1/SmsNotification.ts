import { Notification } from './Notification.js';

export class SmsNotification extends Notification {
  constructor(messageId: string, senderId: string, body: string, deliveryReceipt: boolean) {
    super(messageId, senderId, body, deliveryReceipt);
  }

  protected override channel(): string {
    return 'SMS';
  }

  override dispatch(successful: boolean): string {
    const result = this.dispatchResult(successful);
    return successful ? this.appendReceipt(result) : result;
  }
}
