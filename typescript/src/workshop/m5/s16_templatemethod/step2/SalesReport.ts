import { IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/**
 * Krok 2: Form Template Method - Extract Superclass + Pull Up render(). Szkielet jest "final":
 * kolejność kroków i liczenie sumy należą do bazy, podklasy dostarczają tylko formatowanie.
 * TypeScript nie ma modyfikatora final, więc konstruktor bazy odrzuca podklasę nadpisującą render().
 */
export abstract class SalesReport {
  constructor() {
    if (this.render !== SalesReport.prototype.render) {
      throw new IllegalStateError('render() is final');
    }
  }

  render(sales: readonly Sale[]): string {
    const sorted = [...sales];
    sorted.sort((a, b) => a.time.compareTo(b.time));
    let text = this.header();
    let tickets = 0;
    let total = Money.ZERO;
    for (const sale of sorted) {
      text += this.row(sale);
      tickets += sale.tickets;
      total = total.plus(sale.amount);
    }
    return text + this.footer(tickets, total);
  }

  protected abstract header(): string;

  protected abstract row(sale: Sale): string;

  protected abstract footer(tickets: number, total: Money): string;
}
