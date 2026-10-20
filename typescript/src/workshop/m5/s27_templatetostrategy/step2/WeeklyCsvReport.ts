import { CsvFormat } from './CsvFormat.js';
import { WeeklyPeriod } from './WeeklyPeriod.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class). */
export class WeeklyCsvReport extends ScreeningReport {
  constructor() {
    super(new WeeklyPeriod(), new CsvFormat());
  }
}
