import type { Money } from '../../../shared/Money.js';

/**
 * Statyczny terminal płatniczy. Karty kończące się na 0000 są odrzucane.
 * Obciążeń nie da się podejrzeć z testu.
 */
export class CardTerminal {
  private constructor() {}

  static charge(card: string, _amount: Money): boolean {
    return !card.endsWith('0000');
  }
}
