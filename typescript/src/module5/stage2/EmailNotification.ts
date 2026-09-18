import { Notification } from './Notification.js';

export class EmailNotification extends Notification {
  // Przeciążenie z Javy: czwarty parametr zostaje dla zgodności ze starymi wywołaniami i jest ignorowany.
  constructor(messageId: string, senderId: string, body: string, ignoredDeliveryReceipt?: boolean);
  constructor(messageId: string, senderId: string, body: string) {
    super(messageId, senderId, body);
  }

  protected override channel(): string {
    return 'EMAIL';
  }

  override dispatch(successful: boolean): string {
    return this.dispatchResult(successful);
  }
}
