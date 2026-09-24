import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';

/**
 * Krok 3 (rozwiązanie): kontrakt roli bez zmian. Interfejs TS nie ma metod domyślnych, więc
 * odpowiednikiem `default Money vatAmount()` jest funkcja obok interfejsu, która przyjmuje rolę.
 * Opublikowane implementacje nie muszą nic robić, a funkcja używa wyłącznie operacji kontraktu,
 * więc jest poprawna dla każdej z nich. (Nowa WYMAGANA metoda interfejsu złamałaby kompilację
 * każdej istniejącej implementacji.)
 */
export interface Priceable {
  price(): Money;

  vatPercent(): number;
}

/** Kwota VAT zawarta w cenie brutto: brutto * stawka / (100 + stawka). */
export function vatAmount(item: Priceable): Money {
  const rate = new Decimal(item.vatPercent());
  return new Money(item.price().amount.times(rate)
    .dividedBy(rate.plus(100)).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
}
