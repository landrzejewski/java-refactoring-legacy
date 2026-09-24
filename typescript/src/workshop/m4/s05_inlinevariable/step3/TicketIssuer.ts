import type { Clock, LocalDateTime } from '../../../shared/time.js';
import { Ticket } from '../Ticket.js';

const HOLD_MINUTES = 15;
const ONLINE_FEE_GROSZE = 200n;

/**
 * Krok 3 (rozwiązanie): najpierw Rename przeciążenia `money(grosze: bigint)` -> `moneyFromGrosze`,
 * dopiero potem Inline Variable `price`. Bez Rename wklejenie, które zgubi konwersję Number(...),
 * dałoby `money(basePrice(format))` - kompilator wybrałby przeciążenie bigint (grosze) i cena 40 zł
 * stałaby się "0.40". Po Rename taki kod się nie kompiluje. number i issuedAt zostają - celowo.
 */
export class TicketIssuer {
  private lastNumber = 0;

  constructor(private readonly clock: Clock) {}

  issue(screeningCode: string, format: number): Ticket {
    const number = this.nextNumber();
    const issuedAt: LocalDateTime = this.clock.now();
    const code = screeningCode + '-' + number;
    return new Ticket(code,
      'Bilet ' + code + ', cena ' + money(Number(basePrice(format)))
        + ', oplata ' + moneyFromGrosze(ONLINE_FEE_GROSZE),
      issuedAt, issuedAt.plusMinutes(HOLD_MINUTES));
  }

  private nextNumber(): number {
    this.lastNumber++;
    return this.lastNumber;
  }
}

/** Cena bazowa w pełnych złotych - liczba całkowita (bigint, jak int w starym cenniku). */
function basePrice(format: number): bigint {
  switch (format) {
    case 3: return 40n;
    case 2: return 32n;
    default: return 25n;
  }
}

/** Kwota w złotych. */
function money(zloty: number): string {
  return zloty.toFixed(2);
}

/** Kwota w groszach - osobna nazwa, osobna jednostka. */
function moneyFromGrosze(grosze: bigint): string {
  return `${grosze / 100n}.${String(grosze % 100n).padStart(2, '0')}`;
}
