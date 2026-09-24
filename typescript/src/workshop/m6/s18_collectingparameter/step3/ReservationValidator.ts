import type { ReservationDraft } from '../ReservationDraft.js';
import { Warnings } from './Warnings.js';

/**
 * Krok 3: parametr zbierający ma własny, wąski typ Warnings (tylko add). Właścicielem
 * kolekcji jest validate - tworzy ją i decyduje o formacie wyniku.
 */
export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    const warnings = new Warnings();
    this.checkEmail(draft.email, warnings);
    this.checkSeats(draft.seats, warnings);
    this.checkShowTime(draft, warnings);
    return warnings.summary();
  }

  private checkEmail(email: string | null, warnings: Warnings): void {
    if (email === null || email.trim() === '') {
      warnings.add('brak e-maila');
    } else if (!email.includes('@')) {
      warnings.add(`niepoprawny e-mail: ${email}`);
    }
  }

  private checkSeats(seats: readonly string[], warnings: Warnings): void {
    if (seats.length === 0) {
      warnings.add('brak miejsc');
      return;
    }
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        warnings.add(`miejsce ${seat} zdublowane`);
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      warnings.add('grupa 10+: zastosuj rabat grupowy');
    }
  }

  private checkShowTime(draft: ReservationDraft, warnings: Warnings): void {
    if (!draft.now.isBefore(draft.showStart)) {
      warnings.add('seans juz sie rozpoczal');
    }
  }
}
