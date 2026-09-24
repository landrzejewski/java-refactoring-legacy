import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

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
 * Krok 2: singleton jako zamrożona stała modułu (odpowiednik enum singletona z Javy) - moduł ES
 * jest ewaluowany raz, a obiektu nie da się zmienić ani utworzyć drugi raz. Ale to jedna instancja
 * na graf modułów (dwie kopie pakietu w node_modules albo worker to osobne instancje), nie "jedna na proces".
 */
export const PriceList = Object.freeze({
  basePrice(format: string): Money {
    const price = prices.get(format);
    if (price === undefined) {
      throw new IllegalArgumentError(`unknown format: ${format}`);
    }
    return price;
  },
});
