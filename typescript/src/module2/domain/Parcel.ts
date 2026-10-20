import type { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class Parcel {
  readonly weightKg: Decimal;

  constructor(weightKg: Decimal) {
    requireNonNull(weightKg, 'weightKg');

    if (weightKg.lte(0)) {
      throw new IllegalArgumentError('Weight must be positive');
    }
    this.weightKg = weightKg;
  }
}
