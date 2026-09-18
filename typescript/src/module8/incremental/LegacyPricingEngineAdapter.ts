import { requireNonNull } from '../../shared/requireNonNull.js';
import { LegacyPriceCalculator } from './LegacyPriceCalculator.js';
import { PriceQuote } from './PriceQuote.js';
import type { PriceRequest } from './PriceRequest.js';
import type { PricingEngine } from './PricingEngine.js';

export class LegacyPricingEngineAdapter implements PricingEngine {
  private readonly calculator: LegacyPriceCalculator;

  constructor(calculator: LegacyPriceCalculator = new LegacyPriceCalculator()) {
    this.calculator = requireNonNull(calculator, 'calculator');
  }

  quote(request: PriceRequest): PriceQuote {
    requireNonNull(request, 'request');
    return new PriceQuote(
      this.calculator.calculate(
        request.unitPrice,
        request.quantity,
        request.discountRate.times(100), // movePointRight(2)
      ),
    );
  }
}
