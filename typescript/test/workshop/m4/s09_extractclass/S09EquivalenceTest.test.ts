import { describe } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { BookingInput } from '../../../../src/workshop/m4/s09_extractclass/BookingInput.js';
import * as start from '../../../../src/workshop/m4/s09_extractclass/start/Booking.js';
import * as step1 from '../../../../src/workshop/m4/s09_extractclass/step1/Booking.js';
import * as step2 from '../../../../src/workshop/m4/s09_extractclass/step2/Booking.js';
import * as step3 from '../../../../src/workshop/m4/s09_extractclass/step3/Booking.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

interface BookingApi {
  pay(card: string): void;
  summary(): string;
}

function run(input: BookingInput, booking: BookingApi): string {
  let errors = '';
  for (const card of input.cards) {
    try {
      booking.pay(card);
    } catch (error) {
      if (!(error instanceof IllegalStateError)) {
        throw error;
      }
      errors += 'BLAD: ' + error.message + '\n';
    }
  }
  return booking.summary() + errors;
}

/** Test równoważności: to samo podsumowanie i te same błędy płatności po każdym Extract Class. */
describe('S09EquivalenceTest', () => {
  describe('everyStepSummarizesBookingsTheSameWay', () => {
    Scene.variants<BookingInput, string>()
      .variant('start', (i) => run(i, new start.Booking(i.id, i.name, i.email, i.phone, i.amount)))
      .variant('step1', (i) => run(i, new step1.Booking(i.id, i.name, i.email, i.phone, i.amount)))
      .variant('step2', (i) => run(i, new step2.Booking(i.id, i.name, i.email, i.phone, i.amount)))
      .variant('step3', (i) => run(i, new step3.Booking(i.id, i.name, i.email, i.phone, i.amount)))
      .expect('nieopłacona, e-mail do normalizacji',
        new BookingInput('B-1', 'Anna Nowak', ' Anna@Kino.PL ', '600 100 200',
          Money.of('74.00'), []), `Rezerwacja B-1
Klient: Anna Nowak <anna@kino.pl>, tel. 600-100-200
Kwota: 74.00
Platnosc: oczekuje
`)
      .expect('opłacona kartą, telefon z prefiksem',
        new BookingInput('B-2', 'Jan Kowalski', 'jan@kino.pl', '+48 600-100-201',
          Money.of('31.60'), ['4111 1111 1111 1234']), `Rezerwacja B-2
Klient: Jan Kowalski <jan@kino.pl>, tel. 600-100-201
Kwota: 31.60
Platnosc: oplacona karta **** 1234
`)
      .expect('druga płatność odrzucona',
        new BookingInput('B-3', 'Jan Kowalski', 'jan@kino.pl', '600100202',
          Money.of('25.00'), ['4111 1111 1111 1234', '5500 0000 0000 0004']),
        `Rezerwacja B-3
Klient: Jan Kowalski <jan@kino.pl>, tel. 600-100-202
Kwota: 25.00
Platnosc: oplacona karta **** 1234
BLAD: Rezerwacja B-3 jest juz oplacona
`)
      .tests();
  });
});
