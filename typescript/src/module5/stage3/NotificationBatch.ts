import { requireNonNull } from '../../shared/requireNonNull.js';
import type { OutboundNotification } from './OutboundNotification.js';

export class NotificationBatch {
  dispatchAll(
    notifications: readonly OutboundNotification[],
    successful: boolean,
  ): readonly string[] {
    requireNonNull(notifications, 'notifications must not be null');
    // Najpierw walidacja całej listy, dopiero potem wysyłka (nic nie zostaje wysłane częściowo).
    const validatedNotifications = notifications.map((notification) =>
      requireNonNull(notification, 'notification must not be null'));
    return Object.freeze(
      validatedNotifications.map((notification) => notification.dispatch(successful)),
    );
  }
}
