import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ReleaseMessage } from './ReleaseMessage.js';
import type { ReleaseNotifier } from './ReleaseNotifier.js';

export class NotificationService {
  private readonly notifier: ReleaseNotifier;

  constructor(notifier: ReleaseNotifier) {
    this.notifier = requireNonNull(notifier, 'notifier');
  }

  notify(message: ReleaseMessage): string {
    return this.notifier.send(requireNonNull(message, 'message'));
  }
}
