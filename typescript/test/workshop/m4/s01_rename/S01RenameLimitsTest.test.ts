import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m4/s01_rename/start/SalesReport.js';
import * as step3 from '../../../../src/workshop/m4/s01_rename/step3/SalesReport.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/** Granice automatycznego Rename: nazwy, których IDE nie widzi jako użyć symbolu. */
describe('S01RenameLimitsTest', () => {
  it('reflectionTurnsJavaNamesIntoTheCsvHeader', () => {
    expect(header(new start.Line('', 0, Money.ZERO))).toBe('t;n;d');
    // Po Rename Object.keys dałoby nowy nagłówek - dlatego step3 ma jawny CSV_HEADER.
    expect(header(new step3.Line('', 0, Money.ZERO))).toBe('title;tickets;revenue');
  });

  it('configurationStillNamesTheOldMethod', () => {
    const report = step3.SalesReport.prototype as unknown as Record<string, unknown>;
    expect(typeof report['calc2']).toBe('function');
    expect(typeof report['revenueCsv']).toBe('function');
    // Wywołanie po nazwie szuka nazwy z tekstu; bez delegatu calc2 zadanie przestaje działać.
    expect(report['revenue']).toBeUndefined();
  });
});

function header(line: object): string {
  return Object.keys(line).join(';');
}
