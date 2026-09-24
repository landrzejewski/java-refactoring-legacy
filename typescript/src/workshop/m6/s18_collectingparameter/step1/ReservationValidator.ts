import type { ReservationDraft } from '../ReservationDraft.js';

/**
 * Krok 1: String zastąpiony listą - separator dokłada tylko join. Metody wciąż
 * tworzą własne listy, które validate skleja przez push(...).
 */
export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    const warnings: string[] = [];
    warnings.push(...this.checkEmail(draft.email));
    warnings.push(...this.checkSeats(draft.seats));
    if (!draft.now.isBefore(draft.showStart)) {
      warnings.push('seans juz sie rozpoczal');
    }
    return warnings.length === 0 ? 'OK' : warnings.join('; ');
  }

  private checkEmail(email: string | null): readonly string[] {
    if (email === null || email.trim() === '') {
      return ['brak e-maila'];
    }
    if (!email.includes('@')) {
      return [`niepoprawny e-mail: ${email}`];
    }
    return [];
  }

  private checkSeats(seats: readonly string[]): readonly string[] {
    if (seats.length === 0) {
      return ['brak miejsc'];
    }
    const result: string[] = [];
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        result.push(`miejsce ${seat} zdublowane`);
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      result.push('grupa 10+: zastosuj rabat grupowy');
    }
    return result;
  }
}
