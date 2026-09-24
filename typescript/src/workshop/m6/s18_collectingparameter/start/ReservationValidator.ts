import type { ReservationDraft } from '../ReservationDraft.js';

/**
 * Start: ostrzeżenia sklejane w String - każda metoda pomocnicza zwraca fragment z "; ",
 * a na końcu obcinamy dwa ostatnie znaki. Łatwo o zgubiony separator.
 */
export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    let warnings = '';
    warnings += this.checkEmail(draft.email);
    warnings += this.checkSeats(draft.seats);
    if (!draft.now.isBefore(draft.showStart)) {
      warnings += 'seans juz sie rozpoczal; ';
    }
    return warnings === '' ? 'OK' : warnings.substring(0, warnings.length - 2);
  }

  private checkEmail(email: string | null): string {
    if (email === null || email.trim() === '') {
      return 'brak e-maila; ';
    }
    if (!email.includes('@')) {
      return `niepoprawny e-mail: ${email}; `;
    }
    return '';
  }

  private checkSeats(seats: readonly string[]): string {
    if (seats.length === 0) {
      return 'brak miejsc; ';
    }
    let result = '';
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        result += `miejsce ${seat} zdublowane; `;
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      result += 'grupa 10+: zastosuj rabat grupowy; ';
    }
    return result;
  }
}
