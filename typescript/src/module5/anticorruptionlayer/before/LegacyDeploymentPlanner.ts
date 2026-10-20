import { requireNonNull } from '../../../shared/requireNonNull.js';
import { LocalDate } from '../../support.js';
import type { LegacyCmdbClient } from '../LegacyCmdbClient.js';

/**
 * Logika domenowa mówi językiem CMDB: porównuje kody "P", flagi 1
 * i parsuje daty. Każda zmiana w modelu CMDB przecieka do reguł planowania.
 */
export class LegacyDeploymentPlanner {
  private readonly cmdb: LegacyCmdbClient;

  constructor(cmdb: LegacyCmdbClient) {
    this.cmdb = requireNonNull(cmdb, 'cmdb');
  }

  canaryTargets(): readonly string[] {
    return Object.freeze(
      this.cmdb.fetchAll()
        .filter(record => record.statFlg === 1)
        .filter(record => record.envCd === 'T' || record.envCd === 'D')
        .map(record => record.nm)
        .sort(),
    );
  }

  staleEnvironments(today: LocalDate): readonly string[] {
    const threshold = today.minusDays(30);
    return Object.freeze(
      this.cmdb.fetchAll()
        .filter(record => record.statFlg === 1)
        .filter(record => LocalDate.parseBasicDate(record.lstDplyDt).isBefore(threshold))
        .map(record => record.nm)
        .sort(),
    );
  }

  isProduction(name: string): boolean {
    return this.cmdb.fetchAll().some(record => record.nm === name && record.envCd === 'P');
  }
}
