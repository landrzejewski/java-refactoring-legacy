import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { LegacyMessageGateway } from './LegacyMessageGateway.js';
import type { ReleaseMessage } from './ReleaseMessage.js';
import type { ReleaseNotifier } from './ReleaseNotifier.js';

export class LegacyGatewayAdapter implements ReleaseNotifier {
  private readonly gateway: LegacyMessageGateway;

  constructor(gateway: LegacyMessageGateway) {
    this.gateway = requireNonNull(gateway, 'gateway');
  }

  send(message: ReleaseMessage): string {
    requireNonNull(message, 'message');
    return this.gateway.transmit(message.recipient, 'release:' + message.releaseId);
  }
}
