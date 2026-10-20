import { Decimal } from 'decimal.js';
import { requireNonNull } from '../../shared/requireNonNull.js';
import { PriceQuote } from './PriceQuote.js';
import type { PriceRequest } from './PriceRequest.js';
import type { PricingEngine } from './PricingEngine.js';

export class CandidatePricingEngine implements PricingEngine {
  private static readonly MONEY_SCALE = 2;
  private static readonly ROUNDING = Decimal.ROUND_HALF_EVEN;

  quote(request: PriceRequest): PriceQuote {
    requireNonNull(request, 'request');

    const grossAmount = request.unitPrice.times(request.quantity);
    const discountAmount = grossAmount
      .times(request.discountRate)
      .toDecimalPlaces(CandidatePricingEngine.MONEY_SCALE, CandidatePricingEngine.ROUNDING);

    return new PriceQuote(grossAmount.minus(discountAmount));
  }
}
