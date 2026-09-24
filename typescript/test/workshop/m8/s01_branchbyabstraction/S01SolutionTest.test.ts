import { pathToFileURL } from 'node:url';

import { describe, expect, it } from 'vitest';

import { BookingRequest } from '../../../../src/workshop/m8/s01_branchbyabstraction/BookingRequest.js';
import { LegacyTicketPricing } from '../../../../src/workshop/m8/s01_branchbyabstraction/step3/LegacyTicketPricing.js';
import { ModernTicketPricing } from '../../../../src/workshop/m8/s01_branchbyabstraction/step3/ModernTicketPricing.js';
import type { TicketPricing } from '../../../../src/workshop/m8/s01_branchbyabstraction/step3/TicketPricing.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { workshopDir } from '../../support/paths.js';
import { AMATOR, AMATOR_RANO, DIUNA, GROUP_SEATS, KRAINA_LODU } from './S01Fixtures.js';

const requests = [
  new BookingRequest(DIUNA, ['A5', 'A10'], ['N', 'S'], true, false),
  new BookingRequest(KRAINA_LODU, ['B1', 'B2'], ['C', 'N'], false, false),
  new BookingRequest(AMATOR, GROUP_SEATS, Array<string>(10).fill('S'), false, false),
  new BookingRequest(AMATOR_RANO, ['C12', 'C13'], ['E', 'C'], true, false),
];

/** Wspólny test kontraktowy obu implementacji za abstrakcją i dowód zamknięcia migracji. */
describe('S01SolutionTest', () => {
  it('legacyAndModernPricingFulfilTheSameContract', () => {
    const legacy: TicketPricing = new LegacyTicketPricing();
    const modern: TicketPricing = new ModernTicketPricing();
    for (const request of requests) {
      expect(legacy.total(request).equals(modern.total(request)), JSON.stringify(request.seats)).toBe(true);
    }
  });

  it('modernPricingUsesMoneyWithScaleTwo', () => {
    const total = new ModernTicketPricing().total(requests[2]!);
    // 10 x (25 - 25%) = 187.50, grupa -10% = 168.75
    expect(total.equals(Money.of('168.75'))).toBe(true);
    expect(total.amount.decimalPlaces()).toBeLessThanOrEqual(2);
  });

  it('migrationIsClosedOnlyWhenTheOldPathIsGone', async () => {
    // Odpowiednik Class.forName: moduły starej ścieżki nie dają się już załadować.
    const step4 = (name: string) => pathToFileURL(workshopDir('m8', 's01_branchbyabstraction', 'step4', `${name}.ts`)).href;
    await expect(import(step4('LegacyTicketPricing'))).rejects.toThrow();
    await expect(import(step4('PricingMode'))).rejects.toThrow();
  });
});
