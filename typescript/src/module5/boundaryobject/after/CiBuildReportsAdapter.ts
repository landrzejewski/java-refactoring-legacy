import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { LegacyCiClient } from '../LegacyCiClient.js';
import type { BuildReport } from './BuildReport.js';
import { BuildReportMapper } from './BuildReportMapper.js';
import type { BuildReports } from './BuildReports.js';

/**
 * Adapter na granicy: implementuje port domenowy, woła zewnętrzny klient i od razu
 * zamienia ładunek na obiekt graniczny.
 */
export class CiBuildReportsAdapter implements BuildReports {
  private readonly ciClient: LegacyCiClient;

  constructor(ciClient: LegacyCiClient) {
    this.ciClient = requireNonNull(ciClient, 'ciClient');
  }

  find(buildId: string): BuildReport {
    return BuildReportMapper.fromPayload(this.ciClient.fetchBuild(buildId));
  }
}
