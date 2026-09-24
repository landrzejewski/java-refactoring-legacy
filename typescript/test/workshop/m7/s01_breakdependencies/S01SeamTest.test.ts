import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { PaidBooking } from '../../../../src/workshop/m7/s01_breakdependencies/PaidBooking.js';
import * as start from '../../../../src/workshop/m7/s01_breakdependencies/start/ShowtimeReminderJob.js';
import type { BookingStore } from '../../../../src/workshop/m7/s01_breakdependencies/step1/BookingStore.js';
import * as step1 from '../../../../src/workshop/m7/s01_breakdependencies/step1/ShowtimeReminderJob.js';
import * as step2 from '../../../../src/workshop/m7/s01_breakdependencies/step2/ShowtimeReminderJob.js';
import * as step3 from '../../../../src/workshop/m7/s01_breakdependencies/step3/ShowtimeReminderJob.js';
import * as step4 from '../../../../src/workshop/m7/s01_breakdependencies/step4/ShowtimeReminderJob.js';
import { type Clock, fixedClock, LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/**
 * Scena s01: start jest celowo nietestowalny. Każdy krok otwiera kolejny seam,
 * a test rośnie razem z nim: separacja bazy, kontrola czasu, obserwacja maili.
 */
const NOW = LocalDateTime.of(2026, 3, 10, 18, 0);

const list = (items: readonly string[]): string => `[${items.join(', ')}]`;

function clock(): Clock {
  return fixedClock(NOW);
}

function booking(id: string, startAt: LocalDateTime, reminded: boolean): PaidBooking {
  return new PaidBooking(id, 'anna@kino.pl', 'Diuna', startAt, reminded);
}

/**
 * Ręczny fake bazy: podaje rezerwacje i zapamiętuje oznaczone jako przypomniane.
 * Interfejsy BookingStore z kroków mają ten sam kształt, więc w TS (typowanie strukturalne)
 * jeden fake pasuje do każdego kroku.
 */
class FakeStore implements BookingStore {
  readonly marked: string[] = [];

  constructor(private readonly bookings: readonly PaidBooking[]) {}

  paidBookings(): readonly PaidBooking[] {
    return this.bookings;
  }

  markReminded(bookingId: string): void {
    this.marked.push(bookingId);
  }
}

function runStep3(bookings: readonly PaidBooking[]): string {
  const store = new FakeStore(bookings);
  const mails: string[] = [];
  const job = new (class extends step3.ShowtimeReminderJob {
    protected override sendReminder(to: string, subject: string, body: string): void {
      mails.push(`MAIL ${to} | ${subject} | ${body}`);
    }
  })(() => store, clock());
  return `sent=${job.run()} ${list(mails)} marked=${list(store.marked)}`;
}

function runStep4(bookings: readonly PaidBooking[]): string {
  const store = new FakeStore(bookings);
  const mails: string[] = [];
  const job = new step4.ShowtimeReminderJob(() => store, clock(),
    (to, subject, body) => mails.push(`MAIL ${to} | ${subject} | ${body}`));
  return `sent=${job.run()} ${list(mails)} marked=${list(store.marked)}`;
}

describe('S01SeamTest', () => {
  it('startCannotEvenRunInATest', () => {
    const job = new start.ShowtimeReminderJob();
    expect(() => job.run()).toThrow(IllegalStateError);
    expect(() => job.run()).toThrow(/^brak polaczenia/);
  });

  it('step1KeepsConnectionLifetimeOfProductionConstructor', () => {
    let job: step1.ShowtimeReminderJob | undefined;
    expect(() => { job = new step1.ShowtimeReminderJob(); }).not.toThrow();
    expect(() => job!.run()).toThrow(IllegalStateError);
  });

  it('step1RunsWithFakeStoreWhenNothingIsDue', () => {
    const store = new FakeStore([
      booking('B1', LocalDateTime.of(2020, 1, 1, 20, 0), false),
      booking('B2', LocalDateTime.of(2026, 3, 10, 19, 0), true),
    ]);
    const job = new step1.ShowtimeReminderJob(() => store);
    expect(job.run()).toBe(0);
    expect(store.marked).toEqual([]);
  });

  it('step2ControlsTimeButStillHitsStaticMailer', () => {
    const notDue = new FakeStore([
      booking('B1', NOW.plusMinutes(121), false),
      booking('B2', NOW, false),
    ]);
    const quiet = new step2.ShowtimeReminderJob(() => notDue, clock());
    expect(quiet.run()).toBe(0);

    const due = new FakeStore([booking('B3', NOW.plusMinutes(120), false)]);
    const noisy = new step2.ShowtimeReminderJob(() => due, clock());
    expect(() => noisy.run()).toThrow(IllegalStateError);
    expect(() => noisy.run()).toThrow(/^SMTP/);
  });

  describe('firstRealTestForSteps3And4', () => {
    Scene.variants<readonly PaidBooking[], string>()
      .variant('step3', runStep3)
      .variant('step4', runStep4)
      .expect('seans za 90 minut',
        [booking('B1', NOW.plusMinutes(90), false)],
        'sent=1 [MAIL anna@kino.pl | Przypomnienie: Diuna | Seans zaczyna sie o 19:30]'
          + ' marked=[B1]')
      .expect('granica 120 minut wchodzi, 121 nie',
        [booking('B1', NOW.plusMinutes(120), false), booking('B2', NOW.plusMinutes(121), false)],
        'sent=1 [MAIL anna@kino.pl | Przypomnienie: Diuna | Seans zaczyna sie o 20:00]'
          + ' marked=[B1]')
      .expect('juz przypomniane i juz rozpoczete',
        [booking('B1', NOW.plusMinutes(30), true), booking('B2', NOW.minusMinutes(1), false)],
        'sent=0 [] marked=[]')
      .tests();
  });
});
