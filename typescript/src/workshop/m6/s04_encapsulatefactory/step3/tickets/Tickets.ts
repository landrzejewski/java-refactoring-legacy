import { Money } from '../../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

// Krok 3: klasy konkretne są prywatne dla modułu (brak export) - poza tym plikiem nie da się
// ich utworzyć ani zaimportować. TypeScript nie ma widoczności pakietowej z Javy, więc
// StandardTicket i VipTicket przeniesione z osobnych plików tutaj, obok fabryki.

/** Krok 3: klasa prywatna modułu - zwykłe miejsce (w Javie: rekord pakietowy). */
class StandardTicket implements Ticket {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly row: number,
  ) {}

  price(): Money {
    return this.base;
  }

  describe(): string {
    return `${this.title} r${this.row} ${this.price().toString()}`;
  }
}

/** Krok 3: klasa prywatna modułu, miejsce VIP: +10.00 (w Javie: rekord pakietowy). */
class VipTicket implements Ticket {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly row: number,
  ) {}

  price(): Money {
    return this.base.plus(Money.of('10.00'));
  }

  describe(): string {
    return `${this.title} r${this.row} VIP ${this.price().toString()}`;
  }
}

/** Krok 3: fabryka jest jedynym publicznym wejściem do tworzenia biletów. */
export class Tickets {
  private static readonly VIP_FROM_ROW = 10;

  private constructor() {}

  static forSeat(title: string, base: Money, row: number): Ticket {
    if (row >= Tickets.VIP_FROM_ROW) {
      return new VipTicket(title, base, row);
    }
    return new StandardTicket(title, base, row);
  }
}
