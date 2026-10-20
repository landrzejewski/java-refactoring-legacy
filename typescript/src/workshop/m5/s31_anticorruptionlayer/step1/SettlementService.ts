import { Decimal } from 'decimal.js';

import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { LocalDate } from '../../../shared/time.js';
import type { DistributorFeed } from '../DistributorFeed.js';
import type { DistributorRecord } from '../DistributorRecord.js';
import { FilmLicense } from './FilmLicense.js';

/**
 * Krok 1: reguły liczą się na FilmLicense, a rekord dystrybutora jest tłumaczony w jednej
 * prywatnej metodzie, zaraz po pobraniu. Serwis wciąż jednak zna klienta i format dystrybutora.
 */
export class SettlementService {
  private readonly feed: DistributorFeed;

  constructor(feed: DistributorFeed) {
    this.feed = requireNonNull(feed, 'feed');
  }

  canScreen(filmCode: string, day: LocalDate): boolean {
    const record = this.feed.fetch(filmCode);
    return record !== null && SettlementService.toLicense(record).allowsScreeningOn(day);
  }

  settle(filmCode: string, day: LocalDate, revenue: Money): string {
    const record = this.feed.fetch(filmCode);
    if (record === null) {
      return `BRAK LICENCJI ${filmCode}`;
    }
    const license = SettlementService.toLicense(record);
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

  private static toLicense(record: DistributorRecord): FilmLicense {
    const title = record.titleUpper.charAt(0) + record.titleUpper.substring(1).toLowerCase();
    return new FilmLicense(
      record.filmCode,
      title,
      record.statusCode === 'A',
      parseBasicIsoDate(record.validFrom),
      parseBasicIsoDate(record.validTo),
      Number.parseInt(record.share.replaceAll('%', '').trim(), 10),
      new Money(new Decimal(record.minGuaranteeCents).dividedBy(100)));
  }
}

/** Odpowiednik LocalDate.parse(text, DateTimeFormatter.BASIC_ISO_DATE): "yyyyMMdd" -> LocalDate. */
function parseBasicIsoDate(text: string): LocalDate {
  if (!/^\d{8}$/.test(text)) {
    throw new RangeError(`Text '${text}' could not be parsed`);
  }
  const date = LocalDate.of(Number(text.slice(0, 4)), Number(text.slice(4, 6)), Number(text.slice(6)));
  if (date.toString() !== `${text.slice(0, 4)}-${text.slice(4, 6)}-${text.slice(6)}`) {
    throw new RangeError(`Text '${text}' could not be parsed: invalid date`);
  }
  return date;
}
