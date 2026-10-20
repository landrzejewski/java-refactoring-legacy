import { IllegalStateError } from '../../../../shared/errors.js';

/** Podsystem: zajętość miejsc. Rezerwacja jest atomowa - albo wszystkie miejsca, albo żadne. */
export class SeatRegistry {
  private readonly taken = new Map<string, Set<string>>();

  reserve(screening: string, seats: readonly string[]): void {
    let current = this.taken.get(screening);
    if (current === undefined) {
      current = new Set<string>();
      this.taken.set(screening, current);
    }
    for (const seat of seats) {
      if (current.has(seat)) {
        throw new IllegalStateError(`seat taken ${seat}`);
      }
    }
    for (const seat of seats) {
      current.add(seat);
    }
  }

  release(screening: string, seats: readonly string[]): void {
    const current = this.taken.get(screening);
    if (current !== undefined) {
      seats.forEach((seat) => current.delete(seat));
    }
  }

  // Jak TreeMap<String, TreeSet<String>>.toString() w Javie: klucze i miejsca posortowane.
  describe(): string {
    const screenings = [...this.taken.keys()].sort()
      .map((screening) => `${screening}=[${[...(this.taken.get(screening) ?? [])].sort().join(', ')}]`);
    return `{${screenings.join(', ')}}`;
  }
}
