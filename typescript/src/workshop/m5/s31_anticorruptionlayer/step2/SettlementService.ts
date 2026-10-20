import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import type { DistributorFeed } from '../DistributorFeed.js';
import { DistributorTranslator } from './DistributorTranslator.js';

/**
 * Krok 2: serwis nie zna już konwencji dystrybutora - tłumaczy je DistributorTranslator.
 * Nadal jednak pobiera DistributorRecord i obsługuje jego null; schowamy to za portem w kroku 3.
 */
export class SettlementService {
  private readonly feed: DistributorFeed;
  private readonly translator = new DistributorTranslator();

  constructor(feed: DistributorFeed) {
    this.feed = requireNonNull(feed, 'feed');
  }

  canScreen(filmCode: string, day: LocalDate): boolean {
    const record = this.feed.fetch(filmCode);
    return record !== null && this.translator.toLicense(record).allowsScreeningOn(day);
  }

  settle(filmCode: string, day: LocalDate, revenue: Money): string {
    const record = this.feed.fetch(filmCode);
    if (record === null) {
      return `BRAK LICENCJI ${filmCode}`;
    }
    const license = this.translator.toLicense(record);
    if (!license.active) {
      return `LICENCJA ZAWIESZONA ${filmCode}`;
    }
    if (!license.covers(day)) {
      return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
    }
    return `ROZLICZENIE ${license.title} ${day.toString()}: przychod ${revenue.toString()}, udzial `
      + `${license.sharePercent}%, minimum ${license.minimumGuarantee.toString()}`
      + `, dla dystrybutora ${license.dueFor(revenue).toString()}`;
  }
}
