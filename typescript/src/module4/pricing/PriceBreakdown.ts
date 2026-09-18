import type { Decimal } from 'decimal.js';
import { ArithmeticError, IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

// Rekord z kwotami o skali 2 (setScale(2, UNNECESSARY)); equals() daje równość wartościową.
export class PriceBreakdown {
  readonly baseRentalCost: Decimal;
  readonly discount: Decimal;
  readonly insuranceCost: Decimal;
  readonly deliveryCost: Decimal;
  readonly netAmount: Decimal;
  readonly vat: Decimal;
  readonly total: Decimal;

  constructor(
    baseRentalCost: Decimal,
    discount: Decimal,
    insuranceCost: Decimal,
    deliveryCost: Decimal,
    netAmount: Decimal,
    vat: Decimal,
    total: Decimal,
  ) {
    this.baseRentalCost = PriceBreakdown.money(baseRentalCost, 'baseRentalCost');
    this.discount = PriceBreakdown.money(discount, 'discount');
    this.insuranceCost = PriceBreakdown.money(insuranceCost, 'insuranceCost');
    this.deliveryCost = PriceBreakdown.money(deliveryCost, 'deliveryCost');
    this.netAmount = PriceBreakdown.money(netAmount, 'netAmount');
    this.vat = PriceBreakdown.money(vat, 'vat');
    this.total = PriceBreakdown.money(total, 'total');
  }

  equals(other: PriceBreakdown): boolean {
    return (
      this.baseRentalCost.equals(other.baseRentalCost) &&
      this.discount.equals(other.discount) &&
      this.insuranceCost.equals(other.insuranceCost) &&
      this.deliveryCost.equals(other.deliveryCost) &&
      this.netAmount.equals(other.netAmount) &&
      this.vat.equals(other.vat) &&
      this.total.equals(other.total)
    );
  }

  private static money(amount: Decimal, name: string): Decimal {
    requireNonNull(amount, name);
    if (amount.lt(0)) {
      throw new IllegalArgumentError(`${name} must not be negative`);
    }
    if (amount.decimalPlaces() > 2) {
      // RoundingMode.UNNECESSARY
      throw new ArithmeticError('Rounding necessary');
    }
    return amount;
  }
}
