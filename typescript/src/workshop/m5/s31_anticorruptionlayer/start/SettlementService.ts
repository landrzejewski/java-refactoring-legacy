import { Decimal } from 'decimal.js';

import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { LocalDate } from '../../../shared/time.js';
import type { DistributorFeed } from '../DistributorFeed.js';

/**
 * Start: rozliczenie z dystrybutorem myśli w modelu dystrybutora. Logika domeny (czy wolno
 * grać film, ile się należy) jest przemieszana z dekodowaniem cudzych konwencji: kody statusu,
 * daty "yyyyMMdd", grosze jako tekst, "45%", tytuły wielkimi literami. Każda zmiana formatu
 * u dystrybutora rozlewa się po naszych regułach.
 */
export class SettlementService {
  private readonly feed: DistributorFeed;

  constructor(feed: DistributorFeed) {
    this.feed = requireNonNull(feed, 'feed');
  }

  canScreen(filmCode: string, day: LocalDate): boolean {
    const record = this.feed.fetch(filmCode);
    return record !== null
      && record.statusCode === 'A'
      && !day.isBefore(parseBasicIsoDate(record.validFrom))
      && !day.isAfter(parseBasicIsoDate(record.validTo));
  }

  settle(filmCode: string, day: LocalDate, revenue: Money): string {
    const record = this.feed.fetch(filmCode);
    if (record === null) {
      return `BRAK LICENCJI ${filmCode}`;
    }
    if (record.statusCode !== 'A') {
      return `LICENCJA ZAWIESZONA ${filmCode}`;
    }
    if (!this.canScreen(filmCode, day)) {
      return `LICENCJA NIEWAZNA ${filmCode} w dniu ${day.toString()}`;
    }
    const percent = Number.parseInt(record.share.replaceAll('%', '').trim(), 10);
    const minimum = new Money(new Decimal(record.minGuaranteeCents).dividedBy(100));
    const due = revenue.percent(percent).max(minimum);
    const title = record.titleUpper.charAt(0) + record.titleUpper.substring(1).toLowerCase();
    return `ROZLICZENIE ${title} ${day.toString()}: przychod ${revenue.toString()}, udzial ${percent}`
      + `%, minimum ${minimum.toString()}, dla dystrybutora ${due.toString()}`;
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
