import type { HallBooking } from './HallBooking.js';
import type { PrivateEvent } from './PrivateEvent.js';
import type { Screening } from './Screening.js';

/** Krok 3: jedna pętla po wspólnym typie; publiczna sygnatura conflicts(...) bez zmian. */
export class HallPlanner {
  conflicts(screenings: readonly Screening[], events: readonly PrivateEvent[]): string[] {
    const bookings: HallBooking[] = [...screenings, ...events];
    const result: string[] = [];
    for (let i = 0; i < bookings.length; i++) {
      for (let j = i + 1; j < bookings.length; j++) {
        const a = bookings[i]!;
        const b = bookings[j]!;
        if (a.overlaps(b)) {
          result.push(a.name() + ' x ' + b.name());
        }
      }
    }
    result.sort();
    return result;
  }
}
