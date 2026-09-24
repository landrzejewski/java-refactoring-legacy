import type { Money } from '../../../shared/Money.js';
import type { Effects } from './Effects.js';

export class Charge {
  readonly kind = 'charge';

  constructor(readonly card: string, readonly amount: Money) {}

  describe(): string {
    return `CHARGE ${this.card}: ${this.amount.toString()}`;
  }

  applyTo(port: Effects): void {
    port.charge(this.card, this.amount);
  }
}

export class Save {
  readonly kind = 'save';

  constructor(readonly row: string) {}

  describe(): string {
    return `SAVE ${this.row}`;
  }

  applyTo(port: Effects): void {
    port.save(this.row);
  }
}

export class SendMail {
  readonly kind = 'sendMail';

  constructor(readonly to: string, readonly text: string) {}

  describe(): string {
    return `MAIL ${this.to}: ${this.text}`;
  }

  applyTo(port: Effects): void {
    port.sendMail(this.to, this.text);
  }
}

/** Zamknięty zbiór efektów planu (odpowiednik sealed interface Effect). */
export type Effect = Charge | Save | SendMail;

/**
 * Krok 3: wynik czystego obliczenia - odpowiedź dla klienta i lista efektów DO wykonania.
 * Efekty to dane: można je porównać, zalogować albo wykonać, ale plan sam niczego nie robi.
 */
export class BookingPlan {
  readonly effects: readonly Effect[];

  constructor(readonly result: string, effects: readonly Effect[]) {
    this.effects = Object.freeze([...effects]);
  }
}
