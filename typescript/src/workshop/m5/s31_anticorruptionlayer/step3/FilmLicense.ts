import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';

/**
 * Krok 3: bez zmian - licencja filmu w języku kina - typy domeny (LocalDate, Money, procent jako liczba całkowita),
 * niezmienniki w konstruktorze i reguły przy danych: czy obejmuje dzień, ile się należy.
 */
export class FilmLicense {
  constructor(
    readonly filmCode: string,
    readonly title: string,
    readonly active: boolean,
    readonly validFrom: LocalDate,
    readonly validTo: LocalDate,
    readonly sharePercent: number,
    readonly minimumGuarantee: Money,
  ) {
    requireNonNull(filmCode, 'filmCode');
    requireNonNull(title, 'title');
    requireNonNull(validFrom, 'validFrom');
    requireNonNull(validTo, 'validTo');
    requireNonNull(minimumGuarantee, 'minimumGuarantee');
    if (validTo.isBefore(validFrom)) {
      throw new IllegalArgumentError('license ends before it starts');
    }
    // number to nie int z Javy: NaN i ułamki też są poza zakresem
    if (!Number.isInteger(sharePercent) || sharePercent < 0 || sharePercent > 100) {
      throw new IllegalArgumentError(`share out of range: ${sharePercent}`);
    }
    if (minimumGuarantee.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('minimum guarantee must not be negative');
    }
  }

  covers(day: LocalDate): boolean {
    return !day.isBefore(this.validFrom) && !day.isAfter(this.validTo);
  }

  allowsScreeningOn(day: LocalDate): boolean {
    return this.active && this.covers(day);
  }

  /** Udział dystrybutora, ale nie mniej niż minimalna gwarancja. */
  dueFor(revenue: Money): Money {
    return revenue.percent(this.sharePercent).max(this.minimumGuarantee);
  }
}
