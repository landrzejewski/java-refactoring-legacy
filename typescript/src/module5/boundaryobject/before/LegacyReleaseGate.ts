import { requireNonNull } from '../../../shared/requireNonNull.js';
import { javaDouble, parseDouble, parseInteger } from '../../support.js';
import type { LegacyCiClient } from '../LegacyCiClient.js';

/**
 * Usługa domenowa, która pracuje bezpośrednio na surowym ładunku z CI. Parsowanie
 * i nazwy kluczy są rozsiane po metodach, a błędny ładunek wybucha głęboko w domenie
 * (NumberFormatError; brakujący klucz w Javie to NullPointerException, tu TypeError).
 */
export class LegacyReleaseGate {
  private readonly ciClient: LegacyCiClient;

  constructor(ciClient: LegacyCiClient) {
    this.ciClient = requireNonNull(ciClient, 'ciClient');
  }

  canRelease(buildId: string): boolean {
    const payload = this.ciClient.fetchBuild(buildId)!;
    const succeeded = 'SUCCESS' === payload.get('status')!.trim().toUpperCase();
    const failed = parseInteger(payload.get('failed')!.trim());
    const coverage = parseDouble(payload.get('coverage')!.trim());
    return succeeded && failed === 0 && coverage >= 80.0;
  }

  summary(buildId: string): string {
    const payload = this.ciClient.fetchBuild(buildId)!;
    return payload.get('id')!.trim()
      + ' [' + payload.get('commit')!.trim().toLowerCase() + '] '
      + payload.get('status')!.trim().toUpperCase()
      + ' coverage=' + javaDouble(parseDouble(payload.get('coverage')!.trim())) + '%'
      + ' failed=' + parseInteger(payload.get('failed')!.trim());
  }
}
