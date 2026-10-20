import type { ConsoleCommand } from './ConsoleCommand.js';
import type { Till } from './Till.js';

/** Krok 3: gałąź SELL jako obiekt komendy. */
export class SellCommand implements ConsoleCommand {
  execute(args: string, till: Till): string {
    const [count = '', ...titleParts] = args.split(' ');
    if (titleParts.length === 0 || !/^\d+$/.test(count)) {
      return 'Blad: SELL <liczba> <tytul>';
    }
    const title = titleParts.join(' ');
    const quantity = Number.parseInt(count, 10);
    const price = till.priceOf(title);
    if (price === undefined) {
      return `Blad: nieznany film ${title}`;
    }
    const total = price.times(quantity);
    till.sold(quantity, total);
    return `Sprzedano ${quantity} x ${title} = ${total.toString()}`;
  }
}
