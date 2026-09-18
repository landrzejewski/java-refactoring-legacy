import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class PriceRequest {
  private static readonly MONEY_SCALE = 2;
  private static readonly RATE_SCALE = 4;
  private static readonly MAXIMUM_DISCOUNT_RATE = new Decimal(1);
  private static readonly ROUNDING = Decimal.ROUND_HALF_EVEN;

  readonly unitPrice: Decimal;
  readonly quantity: number;
  readonly discountRate: Decimal;

  constructor(unitPrice: Decimal, quantity: number, discountRate: Decimal) {
    requireNonNull(unitPrice, 'unitPrice');
    requireNonNull(discountRate, 'discountRate');
    if (unitPrice.lt(0)) {
      throw new IllegalArgumentError('unitPrice must not be negative');
    }
    // Java: int — w TS number, więc całkowitość sprawdzamy jawnie.
    if (!Number.isSafeInteger(quantity)) {
      throw new IllegalArgumentError('quantity must be an integer');
    }
    if (quantity <= 0) {
      throw new IllegalArgumentError('quantity must be positive');
    }
    if (discountRate.lt(0) || discountRate.gt(PriceRequest.MAXIMUM_DISCOUNT_RATE)) {
      throw new IllegalArgumentError('discountRate must be between 0 and 1');
    }

    this.unitPrice = unitPrice.toDecimalPlaces(PriceRequest.MONEY_SCALE, PriceRequest.ROUNDING);
    this.quantity = quantity;
    this.discountRate = discountRate.toDecimalPlaces(
      PriceRequest.RATE_SCALE,
      PriceRequest.ROUNDING,
    );
  }

  /** Równość wartościowa jak w rekordzie Javy. */
  equals(other: unknown): boolean {
    return (
      other instanceof PriceRequest &&
      this.unitPrice.equals(other.unitPrice) &&
      this.quantity === other.quantity &&
      this.discountRate.equals(other.discountRate)
    );
  }
}
