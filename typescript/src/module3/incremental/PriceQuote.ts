import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../../shared/errors.js';
import { requireNonNull } from '../../shared/requireNonNull.js';

export class PriceQuote {
  private static readonly MONEY_SCALE = 2;
  private static readonly ROUNDING = Decimal.ROUND_HALF_EVEN;

  readonly netAmount: Decimal;

  constructor(netAmount: Decimal) {
    requireNonNull(netAmount, 'netAmount');
    if (netAmount.lt(0)) {
      throw new IllegalArgumentError('netAmount must not be negative');
    }
    this.netAmount = netAmount.toDecimalPlaces(PriceQuote.MONEY_SCALE, PriceQuote.ROUNDING);
  }

  /** Równość wartościowa jak w rekordzie Javy. */
  equals(other: unknown): boolean {
    return other instanceof PriceQuote && this.netAmount.equals(other.netAmount);
  }

  /** Kwota zawsze ze skalą 2, jak BigDecimal po setScale(2). */
  toString(): string {
    return this.netAmount.toFixed(PriceQuote.MONEY_SCALE);
  }
}
