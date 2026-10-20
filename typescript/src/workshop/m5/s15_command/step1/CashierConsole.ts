import { Money } from '../../../shared/Money.js';

/** Krok 1: Extract Method - ciało każdej gałęzi w osobnej metodzie o tej samej sygnaturze. */
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
      return this.sell(args);
    } else if (command === 'REFUND') {
      return this.refund(args);
    } else if (command === 'REPORT') {
      return this.report(args);
    }
    return `Nieznana komenda: ${name}`;
  }

  private sell(args: string): string {
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
  }

  private refund(args: string): string {
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
  }

  private report(_args: string): string {
    return `Kasa: ${this.cash.toString()}, biletow: ${this.tickets}`;
  }
}
