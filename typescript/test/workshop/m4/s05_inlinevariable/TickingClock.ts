import { type Clock, LocalDateTime } from '../../../../src/workshop/shared/time.js';

/** Ręczny fake zegara: każdy odczyt przesuwa czas o sekundę - jak prawdziwy zegar, tylko przewidywalnie. */
export class TickingClock implements Clock {
  private current: LocalDateTime;

  constructor(start: LocalDateTime) {
    this.current = start;
  }

  now(): LocalDateTime {
    const now = this.current;
    this.current = LocalDateTime.ofEpochMillis(now.toEpochMillis() + 1000);
    return now;
  }
}

/** Wspólna chwila startowa testów sceny (w Javie: S05EquivalenceTest.T0). */
export const T0 = LocalDateTime.of(2026, 9, 25, 18, 0);
