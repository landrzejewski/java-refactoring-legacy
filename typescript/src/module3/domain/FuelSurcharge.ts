import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class FuelSurcharge {
  private readonly rate: Decimal;

  constructor(rate: Decimal) {
    this.rate = requireNonNull(rate, 'rate');

    if (rate.lt(0) || rate.gt(1)) {
      throw new IllegalArgumentError('Fuel surcharge rate must be between zero and one');
    }
  }

  addTo(baseAmount: Decimal): Decimal {
    requireNonNull(baseAmount, 'baseAmount');

    if (baseAmount.lt(0)) {
      throw new IllegalArgumentError('Base amount must not be negative');
    }

    return baseAmount
      .plus(baseAmount.times(this.rate))
      .toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
