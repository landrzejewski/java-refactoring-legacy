import { describe, expect, it } from 'vitest';
import { LegacyGatewayAdapter } from '../../src/module6/adapter/after/LegacyGatewayAdapter.js';
import { NotificationService } from '../../src/module6/adapter/after/NotificationService.js';
import { ReleaseMessage } from '../../src/module6/adapter/after/ReleaseMessage.js';
import {
  type LegacyMessageGateway,
  LegacyNotificationClient,
} from '../../src/module6/adapter/before/LegacyNotificationClient.js';

function captureFailure(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('expected failure');
}

describe('AdapterEquivalenceTest', () => {
  it('adapterMapsThePreferredContractToTheLegacyProtocol', () => {
    const before = new LegacyNotificationClient();
    const legacyGateway: LegacyMessageGateway = {
      transmit: (destination, body) => destination + '|' + body,
    };
    const legacyResult = before.notifyUsingLegacy(legacyGateway, 'ops', 'rel-42');

    const service = new NotificationService(
      new LegacyGatewayAdapter({ transmit: (destination, body) => destination + '|' + body }),
    );

    expect(service.notify(new ReleaseMessage('ops', 'rel-42'))).toBe(legacyResult);
  });

  it('clientCanUseNativeAndAdaptedImplementationsThroughOneInterface', () => {
    const message = new ReleaseMessage('ops', 'rel-42');
    const nativeService = new NotificationService({
      send: value => value.recipient + '|release:' + value.releaseId,
    });
    const adaptedService = new NotificationService(
      new LegacyGatewayAdapter({ transmit: (destination, body) => destination + '|' + body }),
    );

    expect(adaptedService.notify(message)).toBe(nativeService.notify(message));
  });

  it('messageObjectPreservesLegacyValidation', () => {
    const before = new LegacyNotificationClient();

    const legacyFailure = captureFailure(() =>
      before.notifyUsingLegacy({ transmit: (_destination, body) => body }, ' ', 'rel-42'),
    );
    const refactoredFailure = captureFailure(() => new ReleaseMessage(' ', 'rel-42'));

    expect(refactoredFailure.constructor).toBe(legacyFailure.constructor);
    expect(refactoredFailure.message).toBe(legacyFailure.message);
  });
});
