import type { LegacyCmdbRecord } from './LegacyCmdbRecord.js';

/**
 * Zewnętrzny system, którego modelu nie kontrolujemy.
 */
export interface LegacyCmdbClient {
  fetchAll(): readonly LegacyCmdbRecord[];
}
