import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { BookSeats } from '../../../../src/workshop/m3/s12_cleanarchitecture/step4/app/BookSeats.js';
import { BookSeatsCommand } from '../../../../src/workshop/m3/s12_cleanarchitecture/step4/app/BookSeatsCommand.js';
import { Booking } from '../../../../src/workshop/m3/s12_cleanarchitecture/step4/app/Booking.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/**
 * Test przypadku użycia z kroku 4 na adapterach w pamięci (literały obiektów portów):
 * bez HTTP, bez RowStore, bez Outbox. Sprawdza regułę ceny i protokół efektów.
 */
describe('S12UseCaseTest', () => {
  it('savesThenNotifies', () => {
    const saved: string[] = [];
    const notified: string[] = [];
    const useCase = new BookSeats(
      {
        save: (reservation) => {
          saved.push(`${reservation.email} ${reservation.total.toString()}`);
          return 'X-7';
        },
      },
      { reservationCreated: (id) => { notified.push(id); } });

    const booking = useCase.execute(new BookSeatsCommand('anna@kino.pl', '3D', [2, 11]));

    expect(booking).toEqual(new Booking('X-7', Money.of('74.00')));
    expect(saved).toEqual(['anna@kino.pl 74.00']);
    expect(notified).toEqual(['X-7']);
  });

  it('doesNotNotifyWhenSavingFails', () => {
    const notified: string[] = [];
    const useCase = new BookSeats(
      {
        save: () => {
          throw new IllegalStateError('zapis nieudany');
        },
      },
      { reservationCreated: (id) => { notified.push(id); } });

    expect(() => useCase.execute(new BookSeatsCommand('jan@kino.pl', '2D', [1])))
      .toThrow(IllegalStateError);
    expect(notified).toEqual([]);
  });
});
