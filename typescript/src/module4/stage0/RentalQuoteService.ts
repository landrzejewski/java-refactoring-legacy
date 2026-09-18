import { Decimal } from 'decimal.js';
import { EquipmentType } from '../model/EquipmentType.js';
import type { RentalRequest } from '../model/RentalRequest.js';

// Stan wyjściowy: jednoliterowe nazwy, magiczne liczby, obliczenia i formatowanie w jednej metodzie.
// Decimal nie pamięta skali jak BigDecimal, więc toPlainString() odtwarza toFixed(2).
export class RentalQuoteService {
  private readonly rates = new Map<EquipmentType, Decimal>([
    [EquipmentType.DRILL, new Decimal('39.99')],
    [EquipmentType.GENERATOR, new Decimal('120.00')],
  ]);
  private readonly disc = new Decimal('0.10');

  createQuote(r: RentalRequest): string {
    const a = money(this.rates.get(r.equipmentType)!.times(r.days));
    const d = r.days >= 7 ? money(a.times(this.disc)) : money(new Decimal(0));
    const i = r.insurance ? money(new Decimal('8.00').times(r.days)) : money(new Decimal(0));
    const f = r.delivery ? new Decimal('25.00') : money(new Decimal(0));
    const n = money(a.minus(d).plus(i).plus(f));
    const v = money(n.times(new Decimal('0.23')));
    const t = money(n.plus(v));

    const q = 'RENTAL QUOTE\n'
      + 'Customer: '
      + r.customerName.trim().toUpperCase() + '\n'
      + 'Equipment: ' + r.equipmentType + '\n'
      + 'Days: ' + r.days + '\n'
      + 'Base: ' + a.toFixed(2) + '\n'
      + 'Discount: ' + d.toFixed(2) + '\n'
      + 'Insurance: ' + i.toFixed(2) + '\n'
      + 'Delivery: ' + f.toFixed(2) + '\n'
      + 'Net: ' + n.toFixed(2) + '\n'
      + 'VAT: ' + v.toFixed(2) + '\n'
      + 'Total: ' + t.toFixed(2) + '\n';
    return q;
  }
}

function money(amount: Decimal): Decimal {
  return amount.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
}
