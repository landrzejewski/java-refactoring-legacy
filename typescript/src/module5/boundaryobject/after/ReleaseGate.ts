import { requireNonNull } from '../../../shared/requireNonNull.js';
import { javaDouble } from '../../support.js';
import type { BuildReports } from './BuildReports.js';

/**
 * Usługa domenowa operująca wyłącznie na BuildReport.
 */
export class ReleaseGate {
  private readonly reports: BuildReports;

  constructor(reports: BuildReports) {
    this.reports = requireNonNull(reports, 'reports');
  }

  canRelease(buildId: string): boolean {
    const report = this.reports.find(buildId);
    return report.succeeded() && report.failedTests === 0 && report.coverage >= 80.0;
  }

  summary(buildId: string): string {
    const report = this.reports.find(buildId);
    return report.buildId
      + ' [' + report.commit + '] '
      + report.status
      + ' coverage=' + javaDouble(report.coverage) + '%'
      + ' failed=' + report.failedTests;
  }
}
