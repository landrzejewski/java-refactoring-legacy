import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Tariff } from './Tariff.js';

const TARIFF = '2D=25.00;3D=32.00;IMAX=40.00';

function parse(): ReadonlyMap<string, Money> {
  const parsed = new Map<string, Money>();
  for (const entry of TARIFF.split(';')) {
    const [format = '', amount = ''] = entry.split('=');
    parsed.set(format, Money.of(amount));
  }
  return parsed;
}

const prices = parse();

/**
 * Krok 3: singleton bez zmian, ale implementuje Tariff - to domyślna, nie jedyna
 * implementacja. Cykl życia (jedna instancja) to decyzja korzenia kompozycji.
 */
export const PriceList: Tariff = Object.freeze({
  basePrice(format: string): Money {
    const price = prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  },
});
