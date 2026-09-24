import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

/**
 * Krok 1: Limit Instantiation with Singleton (forma klasyczna) - prywatny konstruktor,
 * jedna instancja tworzona przy inicjalizacji klasy. Bezpieczne, bo obiekt jest niemutowalny.
 */
export class PriceList {
  private static readonly TARIFF = '2D=25.00;3D=32.00;IMAX=40.00';
  private static readonly INSTANCE = new PriceList();

  private readonly prices = new Map<string, Money>();

  private constructor() {
    for (const entry of PriceList.TARIFF.split(';')) {
      const [format = '', amount = ''] = entry.split('=');
      this.prices.set(format, Money.of(amount));
    }
  }

  static getInstance(): PriceList {
    return PriceList.INSTANCE;
  }

  basePrice(format: string): Money {
    const price = this.prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  }
}
