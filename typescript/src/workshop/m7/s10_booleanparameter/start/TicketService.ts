import { Decimal } from 'decimal.js';

/**
 * Start: dwie flagi w publicznym API. Wywołanie book('Diuna', 'IMAX', 2, true, false)
 * nie mówi, co znaczy true, a co false - trzeba zajrzeć do sygnatury.
 */
export class TicketService {
  private static readonly GLASSES = new Decimal('3.00');
  private static readonly FEE = new Decimal('2.00');

  book(title: string, format: string, seats: number, online: boolean, ownGlasses: boolean): string {
    let base: Decimal;
    switch (format) {
      case 'IMAX': base = new Decimal('40.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    const count = new Decimal(seats);
    let total = base.times(count);
    if (format === '3D' && !ownGlasses) {
      total = total.plus(TicketService.GLASSES.times(count));
    }
    if (online) {
      total = total.plus(TicketService.FEE.times(count));
    }
    return `${title} ${format} x${seats}${online ? ' online' : ' kasa'}: ${total.toFixed(2)}`;
  }
}
