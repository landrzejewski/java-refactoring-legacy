import type { ReservationDraft } from '../ReservationDraft.js';

/**
 * Krok 2: Move Accumulation to Collecting Parameter - metody dopisują do przekazanej listy
 * zamiast zwracać fragmenty. Nowa reguła to nowa metoda check...(draft, warnings).
 */
export class ReservationValidator {
  validate(draft: ReservationDraft): string {
    const warnings: string[] = [];
    this.checkEmail(draft.email, warnings);
    this.checkSeats(draft.seats, warnings);
    this.checkShowTime(draft, warnings);
    return warnings.length === 0 ? 'OK' : warnings.join('; ');
  }

  private checkEmail(email: string | null, warnings: string[]): void {
    if (email === null || email.trim() === '') {
      warnings.push('brak e-maila');
    } else if (!email.includes('@')) {
      warnings.push(`niepoprawny e-mail: ${email}`);
    }
  }

  private checkSeats(seats: readonly string[], warnings: string[]): void {
    if (seats.length === 0) {
      warnings.push('brak miejsc');
      return;
    }
    const seen = new Set<string>();
    const reported = new Set<string>();
    for (const seat of seats) {
      if (seen.has(seat) && !reported.has(seat)) {
        reported.add(seat);
        warnings.push(`miejsce ${seat} zdublowane`);
      }
      seen.add(seat);
    }
    if (seats.length >= 10) {
      warnings.push('grupa 10+: zastosuj rabat grupowy');
    }
  }

  private checkShowTime(draft: ReservationDraft, warnings: string[]): void {
    if (!draft.now.isBefore(draft.showStart)) {
      warnings.push('seans juz sie rozpoczal');
    }
  }
}
