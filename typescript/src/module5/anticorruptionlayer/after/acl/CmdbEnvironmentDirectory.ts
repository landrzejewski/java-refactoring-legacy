import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { LegacyCmdbClient } from '../../LegacyCmdbClient.js';
import type { Environment } from '../Environment.js';
import type { EnvironmentDirectory } from '../EnvironmentDirectory.js';
import { CmdbTranslator } from './CmdbTranslator.js';

/**
 * Anti-Corruption Layer: adapter implementujący port domenowy + tłumacz modelu.
 * Typy CMDB nie wychodzą poza ten katalog.
 *
 * Facade a ACL: fasada upraszcza interfejs podsystemu, ale zostaje w tym samym modelu
 * (te same pojęcia, mniej wywołań). ACL tłumaczy między dwoma różnymi modelami, aby
 * obcy model nie "zepsuł" języka domeny; często używa fasady lub adaptera w środku.
 */
export class CmdbEnvironmentDirectory implements EnvironmentDirectory {
  private readonly cmdb: LegacyCmdbClient;

  constructor(cmdb: LegacyCmdbClient) {
    this.cmdb = requireNonNull(cmdb, 'cmdb');
  }

  all(): readonly Environment[] {
    return Object.freeze(this.cmdb.fetchAll().map(record => CmdbTranslator.toEnvironment(record)));
  }
}
