/**
 * Krok 2 (rozwiązanie): Encapsulate Collection - zamiast delegata klient dostaje zamrożoną kopię
 * w kolejności wyboru. Wąska fasada: tylko operacje, których klienci naprawdę używają.
 * Świadomie tracimy: bycie instancją Set, delete/clear, iterację i porównania zbioru.
 */
export class SeatSelection {
  readonly #seats = new Set<string>();
  #clicks = 0;

  add(seat: string): boolean {
    this.#clicks++;
    return this.#addToSeats(seat);
  }

  addAll(more: readonly string[]): boolean {
    let changed = false;
    for (const seat of more) {
      changed = this.add(seat) || changed;
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

  seats(): readonly string[] {
    return Object.freeze([...this.#seats]);
  }

  // Set.add zwraca zbiór, a nie boolean jak w Javie - "czy dodano" liczymy sami.
  #addToSeats(seat: string): boolean {
    const before = this.#seats.size;
    this.#seats.add(seat);
    return this.#seats.size !== before;
  }
}
