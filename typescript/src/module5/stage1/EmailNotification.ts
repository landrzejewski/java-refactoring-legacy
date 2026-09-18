import { Notification } from './Notification.js';

export class EmailNotification extends Notification {
  constructor(messageId: string, senderId: string, body: string, deliveryReceipt: boolean) {
    super(messageId, senderId, body, deliveryReceipt);
  }

  protected override channel(): string {
    return 'EMAIL';
  }

  override dispatch(successful: boolean): string {
    return this.dispatchResult(successful);
  }
}
