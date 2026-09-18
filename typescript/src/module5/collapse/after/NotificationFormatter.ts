import { requireNonNull } from '../../../shared/requireNonNull.js';

export class NotificationFormatter {
  format(recipient: string, message: string): string {
    requireNonNull(recipient, 'recipient must not be null');
    requireNonNull(message, 'message must not be null');

    return 'To: ' + recipient + '\nMessage: ' + message;
  }
}
