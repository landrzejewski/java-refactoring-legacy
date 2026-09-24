import type { Money } from '../../../shared/Money.js';

/**
 * Krok 1: Extract Interface z perspektywy klienta (Cart) - rola "coś, co ma cenę brutto i stawkę VAT".
 * Nie kopiujemy title(), seat() ani name(): koszyk ich nie używa.
 */
export interface Priceable {
  price(): Money;

  vatPercent(): number;
}
