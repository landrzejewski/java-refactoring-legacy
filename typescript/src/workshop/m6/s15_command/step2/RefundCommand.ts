import type { ConsoleCommand } from './ConsoleCommand.js';
import type { Till } from './Till.js';

/** Krok 2: gałąź REFUND jako obiekt komendy. */
export class RefundCommand implements ConsoleCommand {
  execute(args: string, till: Till): string {
    const price = till.priceOf(args);
    if (price === undefined) {
      return `Blad: nieznany film ${args}`;
    }
    if (till.tickets() === 0) {
      return 'Blad: brak biletow do zwrotu';
    }
    till.refunded(price);
    return `Zwrot 1 x ${args} = ${price.toString()}`;
  }
}
