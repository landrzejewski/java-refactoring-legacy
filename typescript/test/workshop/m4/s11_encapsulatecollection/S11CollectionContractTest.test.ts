import { describe, expect, it } from 'vitest';

import { UnsupportedOperationError } from '../../../../src/shared/errors.js';
import { Seat } from '../../../../src/workshop/m4/s11_encapsulatecollection/Seat.js';
import * as start from '../../../../src/workshop/m4/s11_encapsulatecollection/start/Booking.js';
import * as step1 from '../../../../src/workshop/m4/s11_encapsulatecollection/step1/Booking.js';
import * as step2 from '../../../../src/workshop/m4/s11_encapsulatecollection/step2/Booking.js';
import * as step3 from '../../../../src/workshop/m4/s11_encapsulatecollection/step3/Booking.js';

const LATER = new Seat(10, 2);

/**
 * Getter jest typowany jako `readonly Seat[]` od kroku 2, więc kompilator i tak nie pozwoli klientowi
 * na push. Rzutujemy na `Seat[]`, żeby sprawdzić kontrakt w czasie wykonania (jak kod JS bez typów).
 */
function describeContract(getter: () => readonly Seat[], ownerAdds: (seat: Seat) => void): string {
  const seenByClient = getter() as Seat[];
  let clientCanModify: boolean;
  try {
    seenByClient.push(new Seat(1, 1));
    seenByClient.splice(seenByClient.findIndex((s) => s.equals(new Seat(1, 1))), 1);
    clientCanModify = true;
  } catch (error) {
    if (!(error instanceof UnsupportedOperationError || error instanceof TypeError)) {
      throw error;
    }
    clientCanModify = false;
  }
  ownerAdds(LATER);
  const seesLaterChanges = seenByClient.some((s) => s.equals(LATER));
  return 'klient zmienia: ' + (clientCanModify ? 'tak' : 'nie')
    + ', widzi zmiany: ' + (seesLaterChanges ? 'tak' : 'nie');
}

/**
 * Trzy kontrakty kolekcji - tabela ze slajdu jako test. Dla każdego wariantu sprawdzamy:
 * czy klient może zmienić listę z gettera i czy WCZEŚNIEJ pobrana lista widzi późniejsze zmiany właściciela.
 * Tu CELOWO nie ma równoważności: step2 i step3 zmieniają kontrakt gettera.
 */
describe('S11CollectionContractTest', () => {
  it('startPublicFieldIsTheLiveMutableList', () => {
    const booking = new start.Booking();
    expect(describeContract(() => booking.seats, (seat) => booking.seats.push(seat)))
      .toBe('klient zmienia: tak, widzi zmiany: tak');
  });

  it('step1GetterStillReturnsTheSameAlias', () => {
    const booking = new step1.Booking();
    expect(describeContract(() => booking.seats, (seat) => booking.addSeat(seat)))
      .toBe('klient zmienia: tak, widzi zmiany: tak');
  });

  it('step2UnmodifiableListIsAReadOnlyLiveView', () => {
    const booking = new step2.Booking();
    expect(describeContract(() => booking.seats, (seat) => booking.addSeat(seat)))
      .toBe('klient zmienia: nie, widzi zmiany: tak');
  });

  it('step3CopyOfIsASnapshot', () => {
    const booking = new step3.Booking();
    expect(describeContract(() => booking.seats, (seat) => booking.addSeat(seat)))
      .toBe('klient zmienia: nie, widzi zmiany: nie');
  });
});
