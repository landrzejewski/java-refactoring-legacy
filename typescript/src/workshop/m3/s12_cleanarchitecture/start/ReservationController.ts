import { IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';

/**
 * Start: kontroler "HTTP" robi wszystko - parsuje parametry, liczy cenę, zapisuje
 * wiersz unknown[], publikuje komunikat i buduje odpowiedź. Reguły biznesowe
 * (cena, VIP, "najpierw zapis, potem powiadomienie") są splecione z formatem
 * żądania i kolumnami tabeli. Przypadku użycia nie da się wywołać bez HTTP i bazy.
 */
export class ReservationController {
  constructor(private readonly db: RowStore, private readonly outbox: Outbox) {}

  handle(params: ReadonlyMap<string, string>): string {
    const email = params.get('email');
    if (email === undefined || email.trim() === '') {
      return '400 brak email';
    }
    const format = params.get('format') ?? '2D';
    const rows = (params.get('rows') ?? '').split(',')
      .filter((s) => s.trim() !== '').map((s) => Number.parseInt(s, 10));
    if (rows.length === 0) {
      return '400 brak miejsc';
    }
    let base: Money;
    switch (format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    let total = Money.of('0.00');
    for (const row of rows) {
      total = total.plus(base);
      if (row >= 10) {
        total = total.plus(Money.of('10.00'));
      }
    }
    let id: string;
    try {
      id = this.db.insert([email, format, rows.length, total]);
    } catch (error) {
      if (error instanceof IllegalStateError) {
        return `503 ${error.message}`;
      }
      throw error;
    }
    this.outbox.publish('reservation-created', `${id};${email};${total.toString()}`);
    return `201 ${id} ${total.toString()}`;
  }
}
