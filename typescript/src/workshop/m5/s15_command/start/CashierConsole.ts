import { Money } from '../../../shared/Money.js';

/**
 * Start: konsola kasjera jako dyspozytor warunkowy - łańcuch if po nazwie komendy, a w każdej
 * gałęzi pełna logika i modyfikacja stanu kasy. Nowa komenda = kolejny else if.
 */
export class CashierConsole {
  private static readonly PRICES: ReadonlyMap<string, Money> = new Map([
    ['Diuna', Money.of('40.00')],
    ['Kraina Lodu', Money.of('32.00')],
    ['Amator', Money.of('25.00')],
  ]);

  private cash = Money.ZERO;
  private tickets = 0;

  handle(line: string): string {
    const [name = '', ...rest] = line.trim().split(' ');
    const command = name.toUpperCase();
    const args = rest.join(' ');
    if (command === 'SELL') {
      const [count = '', ...titleParts] = args.split(' ');
      if (titleParts.length === 0 || !/^\d+$/.test(count)) {
        return 'Blad: SELL <liczba> <tytul>';
      }
      const title = titleParts.join(' ');
      const quantity = Number.parseInt(count, 10);
      const price = CashierConsole.PRICES.get(title);
      if (price === undefined) {
        return `Blad: nieznany film ${title}`;
      }
      const total = price.times(quantity);
      this.cash = this.cash.plus(total);
      this.tickets += quantity;
      return `Sprzedano ${quantity} x ${title} = ${total.toString()}`;
    } else if (command === 'REFUND') {
      const price = CashierConsole.PRICES.get(args);
      if (price === undefined) {
        return `Blad: nieznany film ${args}`;
      }
      if (this.tickets === 0) {
        return 'Blad: brak biletow do zwrotu';
      }
      this.cash = this.cash.minus(price);
      this.tickets--;
      return `Zwrot 1 x ${args} = ${price.toString()}`;
    } else if (command === 'REPORT') {
      return `Kasa: ${this.cash.toString()}, biletow: ${this.tickets}`;
    }
    return `Nieznana komenda: ${name}`;
  }
}
