import type { BuildReport } from './BuildReport.js';

/**
 * Port domenowy: domena prosi o BuildReport, nie o surowy ładunek.
 */
export interface BuildReports {
  find(buildId: string): BuildReport;
}
