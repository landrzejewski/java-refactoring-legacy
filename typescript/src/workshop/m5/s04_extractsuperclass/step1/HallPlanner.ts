import type { PrivateEvent } from './PrivateEvent.js';
import type { Screening } from './Screening.js';

/** Krok 1: bez zmian - klient jeszcze nie korzysta z nowego typu. */
export class HallPlanner {
  conflicts(screenings: readonly Screening[], events: readonly PrivateEvent[]): string[] {
    const result: string[] = [];
    for (let i = 0; i < screenings.length; i++) {
      for (let j = i + 1; j < screenings.length; j++) {
        const a = screenings[i]!;
        const b = screenings[j]!;
        if (a.hall() === b.hall()
            && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
          result.push(a.title() + ' x ' + b.title());
        }
      }
    }
    for (const a of screenings) {
      for (const b of events) {
        if (a.hall() === b.hall()
            && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
          result.push(a.title() + ' x Wynajem: ' + b.client());
        }
      }
    }
    for (let i = 0; i < events.length; i++) {
      for (let j = i + 1; j < events.length; j++) {
        const a = events[i]!;
        const b = events[j]!;
        if (a.hall() === b.hall()
            && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
          result.push('Wynajem: ' + a.client() + ' x Wynajem: ' + b.client());
        }
      }
    }
    result.sort();
    return result;
  }
}
