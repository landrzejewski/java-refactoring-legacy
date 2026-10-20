import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

/**
 * Start: cennik parsowany w konstruktorze. Jest niemutowalny, więc każda instancja jest
 * równoważna - a mimo to klienci tworzą nową przy każdym wywołaniu. Licznik służy do pomiaru.
 */
export class PriceList {
  private static readonly TARIFF = '2D=25.00;3D=32.00;IMAX=40.00';
  private static createdCount = 0;

  private readonly prices = new Map<string, Money>();

  constructor() {
    for (const entry of PriceList.TARIFF.split(';')) {
      const [format = '', amount = ''] = entry.split('=');
      this.prices.set(format, Money.of(amount));
    }
    PriceList.createdCount++;
  }

  static created(): number {
    return PriceList.createdCount;
  }

  basePrice(format: string): Money {
    const price = this.prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  }
}
