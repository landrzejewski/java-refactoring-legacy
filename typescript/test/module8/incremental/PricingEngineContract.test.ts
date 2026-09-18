import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../../src/shared/errors.js';
import { CandidatePricingEngine } from '../../../src/module8/incremental/CandidatePricingEngine.js';
import { LegacyPricingEngineAdapter } from '../../../src/module8/incremental/LegacyPricingEngineAdapter.js';
import type { PriceQuote } from '../../../src/module8/incremental/PriceQuote.js';
import { PriceRequest } from '../../../src/module8/incremental/PriceRequest.js';
import type { PricingEngine } from '../../../src/module8/incremental/PricingEngine.js';

// decimal.js nie przechowuje skali jak BigDecimal: sprawdzamy wartość,
// liczbę miejsc po przecinku (<= 2) i reprezentację kwoty ze skalą 2.
function assertMoney(quote: PriceQuote, expected: string): void {
  expect(quote.netAmount.equals(new Decimal(expected))).toBe(true);
  expect(quote.netAmount.decimalPlaces()).toBeLessThanOrEqual(2);
  expect(quote.toString()).toBe(expected);
}

function assertQuote(
  engine: PricingEngine,
  unitPrice: string,
  quantity: number,
  discountRate: string,
  expected: string,
): void {
  const quote = engine.quote(
    new PriceRequest(new Decimal(unitPrice), quantity, new Decimal(discountRate)),
  );

  assertMoney(quote, expected);
}

const engines: [string, PricingEngine][] = [
  ['legacy implementation behind an adapter', new LegacyPricingEngineAdapter()],
  ['candidate implementation', new CandidatePricingEngine()],
];

describe('PricingEngineContractTest', () => {
  it.each(engines)(
    'everyProductionImplementationCalculatesCanonicalExamples: %s',
    (_description, engine) => {
      assertQuote(engine, '10.00', 3, '0', '30.00');
      assertQuote(engine, '10.00', 3, '1', '0.00');
      assertQuote(engine, '0.01', 1, '0.5', '0.01');
      assertQuote(engine, '19.995', 2, '0.12555', '34.98');
      assertQuote(engine, '0.05', 3, '0.3333', '0.10');
    },
  );

  it.each(engines)(
    'everyProductionImplementationIsDeterministicAndReturnsMoney: %s',
    (_description, engine) => {
      const request = new PriceRequest(new Decimal('17.49'), 7, new Decimal('0.075'));

      const first = engine.quote(request);
      const second = engine.quote(request);

      expect(first.equals(second)).toBe(true);
      assertMoney(first, '113.25');
    },
  );

  it.each(engines)(
    'everyProductionImplementationRejectsMissingRequest: %s',
    (_description, engine) => {
      expect(() => engine.quote(null as unknown as PriceRequest)).toThrow(
        new NullPointerError('request'),
      );
      expect(() => engine.quote(null as unknown as PriceRequest)).toThrow(NullPointerError);
    },
  );
});
