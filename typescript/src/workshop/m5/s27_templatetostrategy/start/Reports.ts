import { IllegalArgumentError } from '../../../../shared/errors.js';
import { DailyCsvReport } from './DailyCsvReport.js';
import { DailyTextReport } from './DailyTextReport.js';
import type { ScreeningReport } from './ScreeningReport.js';
import { WeeklyCsvReport } from './WeeklyCsvReport.js';
import { WeeklyTextReport } from './WeeklyTextReport.js';

/**
 * Stabilny punkt wejścia sceny: raport dla nazwy okresu (DAILY, WEEKLY) i formatu (CSV, TEXT).
 * Start: zagnieżdżony switch wybiera jedną z czterech podklas - iloczyn osi w kodzie.
 */
export class Reports {
  private constructor() {}

  static of(period: string, format: string): ScreeningReport {
    switch (period) {
      case 'DAILY':
        switch (format) {
          case 'CSV':
            return new DailyCsvReport();
          case 'TEXT':
            return new DailyTextReport();
          default:
            throw Reports.unknownFormat(format);
        }
      case 'WEEKLY':
        switch (format) {
          case 'CSV':
            return new WeeklyCsvReport();
          case 'TEXT':
            return new WeeklyTextReport();
          default:
            throw Reports.unknownFormat(format);
        }
      default:
        throw new IllegalArgumentError(`unknown period: ${period}`);
    }
  }

  private static unknownFormat(format: string): IllegalArgumentError {
    return new IllegalArgumentError(`unknown format: ${format}`);
  }
}
