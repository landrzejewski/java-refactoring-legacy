import { describe, expect, it } from 'vitest';

import { UnsupportedOperationError } from '../../../../src/shared/errors.js';
import * as step1 from '../../../../src/workshop/m3/s10_isp/step1/CashDesk.js';
import type { CinemaAdminService } from '../../../../src/workshop/m3/s10_isp/step1/CinemaAdminService.js';
import * as step2 from '../../../../src/workshop/m3/s10_isp/step2/CashDesk.js';
import type { TicketSales } from '../../../../src/workshop/m3/s10_isp/step2/TicketSales.js';

const notUsed = (): never => {
  throw new UnsupportedOperationError();
};

/**
 * ISP widziany z testu klienta: ile trzeba zaimplementować, żeby przetestować kasę?
 * Gruby CinemaAdminService (jak w start; tu z kroku 1, bo start jest edytowany na żywo)
 * - osiem metod, sześć "nie dotyczy". Rola TicketSales - dwie.
 */
describe('S10ClientFakeTest', () => {
  it('fatInterfaceForcesAFakeOfTheWholeBackOffice', () => {
    const fake: CinemaAdminService = {
      sellTicket: () => 'T-9',
      refundTicket: (ticketId) => `zwrot ${ticketId}`,
      dailyRevenue: notUsed,
      ticketsSold: notUsed,
      scheduleScreening: notUsed,
      cancelScreening: notUsed,
      screenings: notUsed,
      updateTicketPrice: notUsed,
    };
    expect(new step1.CashDesk(fake).sell('Amator', 4)).toBe('bilet T-9: Amator, miejsce 4');
  });

  it('step2CashDeskNeedsOnlyItsRole', () => {
    const fake: TicketSales = {
      sellTicket: () => 'T-9',
      refundTicket: (ticketId) => `zwrot ${ticketId}`,
    };
    expect(new step2.CashDesk(fake).sell('Amator', 4)).toBe('bilet T-9: Amator, miejsce 4');
  });
});
