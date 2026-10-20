import { IllegalArgumentError } from '../../../../shared/errors.js';
import { CsvFormat } from './CsvFormat.js';
import { DailyPeriod } from './DailyPeriod.js';
import type { ReportFormat } from './ReportFormat.js';
import type { ReportPeriod } from './ReportPeriod.js';
import { ScreeningReport } from './ScreeningReport.js';
import { TextFormat } from './TextFormat.js';
import { WeeklyPeriod } from './WeeklyPeriod.js';

/**
 * Stabilny punkt wejścia sceny. Krok 3: osie wybierane niezależnie - dwie mapy zamiast
 * zagnieżdżonego switch. Kolejność walidacji (najpierw okres, potem format) jak w start.
 */
export class Reports {
  private static readonly PERIODS: ReadonlyMap<string, ReportPeriod> = new Map<string, ReportPeriod>([
    ['DAILY', new DailyPeriod()],
    ['WEEKLY', new WeeklyPeriod()],
  ]);

  private static readonly FORMATS: ReadonlyMap<string, ReportFormat> = new Map<string, ReportFormat>([
    ['CSV', new CsvFormat()],
    ['TEXT', new TextFormat()],
  ]);

  private constructor() {}

  static of(period: string, format: string): ScreeningReport {
    const chosenPeriod = Reports.PERIODS.get(period);
    if (chosenPeriod === undefined) {
      throw new IllegalArgumentError(`unknown period: ${period}`);
    }
    const chosenFormat = Reports.FORMATS.get(format);
    if (chosenFormat === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return new ScreeningReport(chosenPeriod, chosenFormat);
  }
}
