import { describe } from 'vitest';

import type { Booking } from '../../../../src/workshop/m8/s12_expandcontract/Booking.js';
import { BookingTable } from '../../../../src/workshop/m8/s12_expandcontract/BookingTable.js';
import * as start from '../../../../src/workshop/m8/s12_expandcontract/start/BookingRepository.js';
import * as step1 from '../../../../src/workshop/m8/s12_expandcontract/step1/BookingRepository.js';
import * as step2 from '../../../../src/workshop/m8/s12_expandcontract/step2/BookingRepository.js';
import * as step3 from '../../../../src/workshop/m8/s12_expandcontract/step3/BookingRepository.js';
import * as step4 from '../../../../src/workshop/m8/s12_expandcontract/step4/BookingRepository.js';
import { Scene } from '../../support/scene.js';
import { ANNA, JAN } from './S12Fixtures.js';

interface Repository {
  save(booking: Booking): void;
  find(id: string): Booking | undefined;
}

function roundTrip(repository: Repository, booking: Booking): Booking | undefined {
  repository.save(booking);
  return repository.find(booking.id);
}

/** Test równoważności: w każdym kroku zapisana rezerwacja wraca w niezmienionej postaci. */
describe('S12EquivalenceTest', () => {
  describe('everyStepReadsBackWhatItSaved', () => {
    Scene.variants<Booking, Booking | undefined>()
      .variant('start', (booking) => roundTrip(new start.BookingRepository(new BookingTable()), booking))
      .variant('step1', (booking) => roundTrip(new step1.BookingRepository(new BookingTable()), booking))
      .variant('step2', (booking) => roundTrip(new step2.BookingRepository(new BookingTable()), booking))
      .variant('step3', (booking) => roundTrip(new step3.BookingRepository(new BookingTable()), booking))
      .variant('step4', (booking) => roundTrip(new step4.BookingRepository(new BookingTable()), booking))
      .expect('dwa miejsca', ANNA, ANNA)
      .expect('jedno miejsce', JAN, JAN)
      .tests();
  });
});
