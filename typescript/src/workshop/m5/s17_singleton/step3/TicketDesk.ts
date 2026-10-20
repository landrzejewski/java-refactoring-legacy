import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import { PriceList } from './PriceList.js';
import type { Tariff } from './Tariff.js';

/**
 * Krok 3: jawne wstrzyknięcie. Konstruktor bez argumentu zachowuje dotychczasowe zachowanie
 * (PriceList), a test lub inny cennik (np. promocyjny) nie wymaga globalnego stanu.
 */
export class TicketDesk {
  private readonly tariff: Tariff;

  constructor(tariff: Tariff = PriceList) {
    this.tariff = requireNonNull(tariff, 'tariff');
  }

  quote(format: string, online: boolean): Money {
    const price = this.tariff.basePrice(format);
    return online ? price.plus(Money.of('2.00')) : price;
  }
}
