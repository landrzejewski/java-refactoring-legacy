/**
 * Krok 1: Replace Inheritance with Delegation. Zbiór jest prywatnym delegatem, addAll() dodaje
 * przez add() delegata - nie trafia już do naszego licznika.
 * Pozostała pułapka delegowania: wygenerowany getter wydaje delegata, więc da się go zmienić z zewnątrz.
 */
export class SeatSelection {
  readonly #seats = new Set<string>();
  #clicks = 0;

  add(seat: string): boolean {
    this.#clicks++;
    return this.#addToSeats(seat);
  }

  addAll(more: readonly string[]): boolean {
    this.#clicks += more.length;
    let changed = false;
    for (const seat of more) {
      changed = this.#addToSeats(seat) || changed;
    }
    return changed;
  }

  contains(seat: string): boolean {
    return this.#seats.has(seat);
  }

  size(): number {
    return this.#seats.size;
  }

  clicks(): number {
    return this.#clicks;
  }

  getSeats(): Set<string> {
    return this.#seats;
  }

  // Set.add zwraca zbiór, a nie boolean jak w Javie - "czy dodano" liczymy sami.
  #addToSeats(seat: string): boolean {
    const before = this.#seats.size;
    this.#seats.add(seat);
    return this.#seats.size !== before;
  }
}
