import { IllegalStateError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Podsystem: bramka płatności. Karty kończące się na 0000 są odrzucane, karta TIMEOUT
 * symuluje awarię bramki (wyjątek, bez wpisu w dzienniku).
 */
export class PaymentClient {
  private readonly entries: string[] = [];

  charge(card: string, amount: Money): boolean {
    if (card === 'TIMEOUT') {
      throw new IllegalStateError('gateway timeout');
    }
    const accepted = !card.endsWith('0000');
    this.entries.push(`${accepted ? 'CHARGED ' : 'DECLINED '}${card} ${amount.toString()}`);
    return accepted;
  }

  log(): readonly string[] {
    return [...this.entries];
  }
}
