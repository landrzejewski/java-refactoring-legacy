import { pathToFileURL } from 'node:url';

import { describe, expect, it } from 'vitest';

import { BookingLedger } from '../../../../src/workshop/m8/s02_stranglerfig/BookingLedger.js';
import * as step1 from '../../../../src/workshop/m8/s02_stranglerfig/step1/CinemaFacade.js';
import * as step2 from '../../../../src/workshop/m8/s02_stranglerfig/step2/CinemaFacade.js';
import * as step3 from '../../../../src/workshop/m8/s02_stranglerfig/step3/CinemaFacade.js';
import { workshopDir } from '../../support/paths.js';

/** Routing fasady krok po kroku i dowód, że stary system zniknął. */
describe('S02SolutionTest', () => {
  it('facadeRoutesMoreOperationsToNewCodeWithEveryStep', () => {
    const ledger = new BookingLedger();
    expect(new step1.CinemaFacade(ledger).routes()).toEqual(new Map([['book', 'legacy'], ['report', 'legacy']]));
    expect(new step2.CinemaFacade(ledger).routes()).toEqual(new Map([['book', 'new'], ['report', 'legacy']]));
    expect(new step3.CinemaFacade(ledger).routes()).toEqual(new Map([['book', 'new'], ['report', 'new']]));
  });

  it('legacyReportSeesBookingsTakenByTheNewModule', () => {
    const ledger = new BookingLedger();
    const facade = new step2.CinemaFacade(ledger);
    facade.book('anna@kino.pl', 'Amator', 1, 1, false);
    expect(facade.report()).toBe(`RAPORT
Amator: 1 bil., 25.00
Biletow: 1
Przychod z biletow: 25.00
Oplaty rezerwacyjne: 0.00
`);
  });

  it('legacyCinemaIsDeletedInTheLastStep', async () => {
    // Odpowiednik Class.forName: modułu LegacyCinema w kroku 4 nie da się już załadować.
    const legacyCinema = pathToFileURL(workshopDir('m8', 's02_stranglerfig', 'step4', 'LegacyCinema.ts')).href;
    await expect(import(legacyCinema)).rejects.toThrow();
  });
});
