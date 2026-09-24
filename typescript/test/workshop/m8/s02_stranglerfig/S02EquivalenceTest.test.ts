import { describe } from 'vitest';

import { BookingLedger } from '../../../../src/workshop/m8/s02_stranglerfig/BookingLedger.js';
import type { CinemaApi } from '../../../../src/workshop/m8/s02_stranglerfig/CinemaApi.js';
import * as start from '../../../../src/workshop/m8/s02_stranglerfig/start/LegacyCinema.js';
import * as step1 from '../../../../src/workshop/m8/s02_stranglerfig/step1/CinemaFacade.js';
import * as step2 from '../../../../src/workshop/m8/s02_stranglerfig/step2/CinemaFacade.js';
import * as step3 from '../../../../src/workshop/m8/s02_stranglerfig/step3/CinemaFacade.js';
import * as step4 from '../../../../src/workshop/m8/s02_stranglerfig/step4/CinemaFacade.js';
import { Scene } from '../../support/scene.js';

function run(system: (ledger: BookingLedger) => CinemaApi, withBookings: boolean): string {
  const api = system(new BookingLedger());
  if (!withBookings) {
    return api.report();
  }
  return api.book('anna@kino.pl', 'Diuna', 3, 2, true) + '\n'
    + api.book('jan@kino.pl', 'Amator', 1, 10, false) + '\n'
    + api.book('ola@kino.pl', 'Kraina Lodu', 2, 0, true) + '\n'
    + api.book('ola@kino.pl', 'Kraina Lodu', 2, 1, true) + '\n'
    + api.report();
}

/**
 * Test równoważności: ten sam scenariusz klienta (rezerwacje + raport) daje ten sam zapis
 * niezależnie od tego, czy operacje obsługuje legacy, nowy moduł czy mieszanka obu.
 */
describe('S02EquivalenceTest', () => {
  describe('clientsSeeTheSameSystemAfterEveryStep', () => {
    Scene.variants<boolean, string>()
      .variant('start', (withBookings) => run((ledger) => new start.LegacyCinema(ledger), withBookings))
      .variant('step1', (withBookings) => run((ledger) => new step1.CinemaFacade(ledger), withBookings))
      .variant('step2', (withBookings) => run((ledger) => new step2.CinemaFacade(ledger), withBookings))
      .variant('step3', (withBookings) => run((ledger) => new step3.CinemaFacade(ledger), withBookings))
      .variant('step4', (withBookings) => run((ledger) => new step4.CinemaFacade(ledger), withBookings))
      .expect('rezerwacje (w tym błędna) i raport', true, `B1
B2
ERROR: no seats
B3
RAPORT
Amator: 10 bil., 225.00
Diuna: 2 bil., 80.00
Kraina Lodu: 1 bil., 32.00
Biletow: 13
Przychod z biletow: 337.00
Oplaty rezerwacyjne: 6.00
`)
      .expect('raport pustej bazy', false, `RAPORT
Biletow: 0
Przychod z biletow: 0.00
Oplaty rezerwacyjne: 0.00
`)
      .tests();
  });
});
