import { CsvFormat } from './CsvFormat.js';
import { DailyPeriod } from './DailyPeriod.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class). */
export class DailyCsvReport extends ScreeningReport {
  constructor() {
    super(new DailyPeriod(), new CsvFormat());
  }
}
