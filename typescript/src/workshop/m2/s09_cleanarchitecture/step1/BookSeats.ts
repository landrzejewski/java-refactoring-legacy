import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import type { BookSeatsCommand } from './BookSeatsCommand.js';
import { Booking } from './Booking.js';

/**
 * Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja jednego celu:
 * wyceń, zapisz, powiadom. Wejście i wyjście to rekordy. Wciąż zna jednak
 * wiersz unknown[] i temat komunikatu (szczegóły techniczne).
 */
export class BookSeats {
  constructor(private readonly db: RowStore, private readonly outbox: Outbox) {}

  execute(command: BookSeatsCommand): Booking {
    if (command.rows.length === 0) {
      throw new IllegalArgumentError('brak miejsc');
    }
    const total = this.price(command);
    const id = this.db.insert([command.email, command.format, command.rows.length, total]);
    this.outbox.publish('reservation-created', `${id};${command.email};${total.toString()}`);
    return new Booking(id, total);
  }

  private price(command: BookSeatsCommand): Money {
    let base: Money;
    switch (command.format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    let total = Money.of('0.00');
    for (const row of command.rows) {
      total = total.plus(base);
      if (row >= 10) {
        total = total.plus(Money.of('10.00'));
      }
    }
    return total;
  }
}
