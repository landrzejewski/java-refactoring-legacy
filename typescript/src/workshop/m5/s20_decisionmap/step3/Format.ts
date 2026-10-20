import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';

/**
 * Krok 3 (ścieżka B - zmienia się zestaw formatów): format jako typ z zachowaniem
 * (odpowiednik enuma z Javy - klasa z instancjami statycznymi). Nowy format (4DX, ScreenX)
 * to jedna stała; wyjątek od reguły dnia - własna wersja priceOn dla tej stałej.
 */
export class Format {
  static readonly TWO_D = new Format('2D', '25.00');
  static readonly THREE_D = new Format('3D', '32.00');
  static readonly IMAX = new Format('IMAX', '40.00');

  private readonly base: Money;

  private constructor(private readonly code: string, base: string) {
    this.base = Money.of(base);
  }

  static values(): readonly Format[] {
    return [Format.TWO_D, Format.THREE_D, Format.IMAX];
  }

  static of(code: string): Format {
    for (const format of Format.values()) {
      if (format.code === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format: ${code}`);
  }

  priceOn(day: DayOfWeek): Money {
    switch (day) {
      case 'TUESDAY': return this.base.minus(this.base.percent(30));
      case 'SATURDAY':
      case 'SUNDAY': return this.base.plus(Money.of('2.00'));
      default: return this.base;
    }
  }
}
