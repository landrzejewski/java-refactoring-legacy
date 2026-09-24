import { describe } from 'vitest';

import type { Reservation } from '../../../../src/workshop/m6/s05_extractfactory/Reservation.js';
import * as start from '../../../../src/workshop/m6/s05_extractfactory/start/ReservationService.js';
import * as step1 from '../../../../src/workshop/m6/s05_extractfactory/step1/ReservationService.js';
import * as step2 from '../../../../src/workshop/m6/s05_extractfactory/step2/ReservationService.js';
import * as step3 from '../../../../src/workshop/m6/s05_extractfactory/step3/ReservationService.js';
import { fixedClock, LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

const CLOCK = fixedClock(LocalDateTime.parse('2026-10-02T16:00:00'));

interface Service {
  reserve(channel: string, email: string, seats: readonly string[]): Reservation;
  reserveGroup(email: string, seats: readonly string[]): Reservation;
}

function play(script: readonly string[], service: Service): string {
  const lines: string[] = [];
  for (const line of script) {
    const [channel = '', email = '', seatList = ''] = line.split(' ');
    const seats = seatList.split(',');
    try {
      const r = channel === 'GROUP'
        ? service.reserveGroup(email, seats)
        : service.reserve(channel, email, seats);
      lines.push(`${r.id} ${r.channel} [${r.seats.join(', ')}] fee=${r.fee.toString()}`
        + ` expires=${r.expiresAt === null ? 'null' : r.expiresAt.toString()}`);
    } catch (error) {
      lines.push(`${(error as Error).name}: ${(error as Error).message}`);
    }
  }
  return `${lines.join('\n')}\n`;
}

/**
 * Scenariusz kilku rezerwacji na jednym serwisie. Linia skryptu: "KANAŁ email miejsca",
 * kanał GROUP oznacza reserveGroup.
 */
describe('S05EquivalenceTest', () => {
  describe('everyStepCreatesTheSameReservations', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', (script) => play(script, new start.ReservationService(CLOCK)))
      .variant('step1', (script) => play(script, new step1.ReservationService(CLOCK)))
      .variant('step2', (script) => play(script, new step2.ReservationService(CLOCK)))
      .variant('step3', (script) => play(script, new step3.ReservationService(CLOCK)))
      .expect('kasa i online', [
        'BOX_OFFICE jan@kino.pl A1,A2',
        'ONLINE anna@kino.pl A3',
      ], `R1 BOX_OFFICE [A1, A2] fee=0.00 expires=null
R2 ONLINE [A3] fee=2.00 expires=2026-10-02T16:15
`)
      .expect('zajęte miejsce nie zużywa numeru, nieznany kanał zużywa', [
        'ONLINE anna@kino.pl A1',
        'ONLINE jan@kino.pl A1',
        'PHONE jan@kino.pl B1',
        'BOX_OFFICE jan@kino.pl B1',
      ], `R1 ONLINE [A1] fee=2.00 expires=2026-10-02T16:15
IllegalStateError: seat taken: A1
IllegalArgumentError: unknown channel: PHONE
R3 BOX_OFFICE [B1] fee=0.00 expires=null
`)
      .expect('grupa', [
        'GROUP jan@kino.pl C1,C2',
        'GROUP jan@kino.pl C1,C2,C3,C4,C5,C6,C7,C8,C9,C10',
      ], `IllegalArgumentError: group needs 10+ seats
R1 ONLINE [C1, C2, C3, C4, C5, C6, C7, C8, C9, C10] fee=20.00 expires=2026-10-02T16:15
`)
      .tests();
  });
});
