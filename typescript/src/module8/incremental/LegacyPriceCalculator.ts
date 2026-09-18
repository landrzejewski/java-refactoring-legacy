import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class LegacyPriceCalculator {
  private static readonly MONEY_SCALE = 2;
  private static readonly PERCENT_SCALE = 2;
  private static readonly MAXIMUM_DISCOUNT_PERCENT = new Decimal('100');
  private static readonly ROUNDING = Decimal.ROUND_HALF_EVEN;

  calculate(unitPrice: Decimal, quantity: number, discountPercent: Decimal): Decimal {
    requireNonNull(unitPrice, 'unitPrice');
    requireNonNull(discountPercent, 'discountPercent');
    if (unitPrice.lt(0)) {
      throw new IllegalArgumentError('unitPrice must not be negative');
    }
    if (quantity <= 0) {
      throw new IllegalArgumentError('quantity must be positive');
    }
    if (discountPercent.lt(0) || discountPercent.gt(LegacyPriceCalculator.MAXIMUM_DISCOUNT_PERCENT)) {
      throw new IllegalArgumentError('discountPercent must be between 0 and 100');
    }

    const normalizedUnitPrice = unitPrice.toDecimalPlaces(
      LegacyPriceCalculator.MONEY_SCALE,
      LegacyPriceCalculator.ROUNDING,
    );
    const normalizedDiscountPercent = discountPercent.toDecimalPlaces(
      LegacyPriceCalculator.PERCENT_SCALE,
      LegacyPriceCalculator.ROUNDING,
    );
    const grossAmount = normalizedUnitPrice.times(quantity);
    const discountAmount = grossAmount
      .times(normalizedDiscountPercent)
      .div(100) // movePointLeft(2)
      .toDecimalPlaces(LegacyPriceCalculator.MONEY_SCALE, LegacyPriceCalculator.ROUNDING);

    return grossAmount
      .minus(discountAmount)
      .toDecimalPlaces(LegacyPriceCalculator.MONEY_SCALE, LegacyPriceCalculator.ROUNDING);
  }
}
