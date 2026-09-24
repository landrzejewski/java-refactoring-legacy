/**
 * Start: wybór miejsc dziedziczy po Set tylko po to, by mieć add/has za darmo, i liczy kliknięcia
 * do analityki. Pułapka self-use: hurtowe addAll() dodaje miejsca przez add() na this, a add() jest
 * nadpisane - więc miejsca dodane hurtem liczą się podwójnie (tak samo sama biblioteka JS: konstruktor
 * Set woła this.add() dla każdego elementu). Do tego klient dostaje całe API zbioru
 * (delete, clear...), które omija licznik.
 */
export class SeatSelection extends Set<string> {
  #clicks = 0;

  override add(seat: string): this {
    this.#clicks++;
    return super.add(seat);
  }

  addAll(seats: readonly string[]): boolean {
    this.#clicks += seats.length;
    const before = this.size;
    seats.forEach(this.add, this);
    return this.size !== before;
  }

  clicks(): number {
    return this.#clicks;
  }
}
