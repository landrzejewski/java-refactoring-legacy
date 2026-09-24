import type { Clock, LocalDateTime } from '../../../shared/time.js';
import { Ticket } from '../Ticket.js';

const HOLD_MINUTES = 15;
const ONLINE_FEE_GROSZE = 200n;

/**
 * Start: wystawianie biletów. Sześć zmiennych lokalnych - część to zbędny szum,
 * ale trzy z nich trzymają wynik, którego NIE wolno obliczyć ponownie:
 * number (efekt uboczny), issuedAt (odczyt zegara), price (konwersja jednostek dla przeciążenia money).
 */
export class TicketIssuer {
  private lastNumber = 0;

  constructor(private readonly clock: Clock) {}

  issue(screeningCode: string, format: number): Ticket {
    const number = this.nextNumber();
    const issuedAt: LocalDateTime = this.clock.now();
    const code = screeningCode + '-' + number;
    const price: number = Number(basePrice(format));
    const label = 'Bilet ' + code + ', cena ' + money(price)
      + ', oplata ' + money(ONLINE_FEE_GROSZE);
    const holdUntil = issuedAt.plusMinutes(HOLD_MINUTES);
    return new Ticket(code, label, issuedAt, holdUntil);
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
function money(zloty: number): string;
/** Kwota w groszach - ta sama nazwa, inna jednostka. */
function money(grosze: bigint): string;
function money(amount: number | bigint): string {
  if (typeof amount === 'bigint') {
    return `${amount / 100n}.${String(amount % 100n).padStart(2, '0')}`;
  }
  return amount.toFixed(2);
}
