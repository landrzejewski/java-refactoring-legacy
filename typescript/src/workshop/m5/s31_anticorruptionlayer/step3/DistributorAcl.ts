import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { LocalDate } from '../../../shared/time.js';
import type { DistributorFeed } from '../DistributorFeed.js';
import type { DistributorRecord } from '../DistributorRecord.js';
import { FilmLicense } from './FilmLicense.js';
import type { FilmLicenses } from './FilmLicenses.js';

/**
 * Krok 3: Anti-Corruption Layer - adapter portu FilmLicenses nad klientem dystrybutora
 * plus translator modelu. Tylko ta warstwa widzi DistributorFeed i DistributorRecord.
 *
 * Facade a ACL: fasada upraszcza dostęp do podsystemu, ale mówi jego językiem
 * (te same pojęcia, mniej wywołań). ACL tłumaczy cudzy model na własny i chroni domenę
 * przed cudzymi pojęciami - często jest zbudowana z fasady, adaptera i translatora naraz.
 */
export class DistributorAcl implements FilmLicenses {
  private readonly feed: DistributorFeed;
  private readonly translator = new DistributorTranslator();

  constructor(feed: DistributorFeed) {
    this.feed = requireNonNull(feed, 'feed');
  }

  find(filmCode: string): FilmLicense | undefined {
    const record = this.feed.fetch(filmCode);
    return record === null ? undefined : this.translator.toLicense(record);
  }
}

/**
 * Krok 3: translator jako część warstwy antykorupcyjnej - klasa modułu bez eksportu
 * (odpowiednik klasy pakietowej z Javy). Zna konwencje dystrybutora: kody statusu,
 * daty "yyyyMMdd", grosze jako tekst, "45%", tytuły wielkimi literami.
 * Zepsuty rekord zgłasza się tutaj, w języku granicy.
 */
class DistributorTranslator {
  private static readonly ACTIVE = 'A';

  toLicense(record: DistributorRecord): FilmLicense {
    return new FilmLicense(
      record.filmCode,
      DistributorTranslator.title(record.titleUpper),
      record.statusCode === DistributorTranslator.ACTIVE,
      DistributorTranslator.date(record.validFrom),
      DistributorTranslator.date(record.validTo),
      Number.parseInt(record.share.replaceAll('%', '').trim(), 10),
      new Money(new Decimal(record.minGuaranteeCents).dividedBy(100)));
  }

  private static title(upper: string): string {
    return upper.charAt(0) + upper.substring(1).toLowerCase();
  }

  private static date(legacy: string): LocalDate {
    try {
      return parseBasicIsoDate(legacy);
    } catch (exception) {
      throw new IllegalArgumentError(`invalid legacy date: ${legacy}`, { cause: exception });
    }
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
