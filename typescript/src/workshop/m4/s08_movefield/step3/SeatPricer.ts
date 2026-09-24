import { Decimal } from 'decimal.js';

import type { Screening } from './Screening.js';

const VIP_SURCHARGE = new Decimal('10.00');

/** Krok 3: wycena pyta seans "czy VIP" jeden raz, zamiast porównywać surowy próg. */
export class SeatPricer {
  quote(screening: Screening, row: number): string {
    let base: Decimal;
    switch (screening.format) {
      case 3: base = new Decimal('40.00'); break;
      case 2: base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    const vip = screening.isVip(row);
    const price = vip ? base.plus(VIP_SURCHARGE) : base;
    return screening.hall.name + ', rzad ' + row + (vip ? ' (VIP)' : '') + ': ' + price.toFixed(2);
  }
}
