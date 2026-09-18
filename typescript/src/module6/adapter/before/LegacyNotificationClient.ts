import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';

// Zagnieżdżone interfejsy Javy (LegacyNotificationClient.ReleaseNotifier,
// LegacyNotificationClient.LegacyMessageGateway) eksportowane z tego samego pliku.
export interface ReleaseNotifier {
  send(recipient: string, releaseId: string): string;
}

export interface LegacyMessageGateway {
  transmit(destination: string, body: string): string;
}

export class LegacyNotificationClient {
  notifyUsingPreferred(notifier: ReleaseNotifier, recipient: string, releaseId: string): string {
    validate(recipient, releaseId);
    return requireNonNull(notifier, 'notifier').send(recipient, releaseId);
  }

  notifyUsingLegacy(gateway: LegacyMessageGateway, recipient: string, releaseId: string): string {
    validate(recipient, releaseId);
    return requireNonNull(gateway, 'gateway').transmit(recipient, 'release:' + releaseId);
  }
}

function validate(recipient: string, releaseId: string): void {
  if (isBlank(recipient)) {
    throw new IllegalArgumentError('recipient must not be blank');
  }
  if (isBlank(releaseId)) {
    throw new IllegalArgumentError('releaseId must not be blank');
  }
}
