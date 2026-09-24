export class Route {
  constructor(
    readonly operation: string,
    readonly target: string,
    readonly owner: string,
    readonly removeWhen: string,
  ) {}
}

/**
 * Krok 2: każda trasa ma właściciela i kryterium usunięcia - element przejściowy bez nich
 * staje się nowym legacy. Routing (zachowanie) bez zmian.
 */
export class Routing {
  private constructor() {}

  static routes(): readonly Route[] {
    return Object.freeze([
      new Route('book', 'new', 'zespol Sprzedaz', '-'),
      new Route('report', 'new', 'zespol Raporty', '-'),
      new Route('cancel', 'legacy', 'zespol Sprzedaz',
        'CancelModule w trybie CANDIDATE przez 14 dni'),
    ]);
  }
}
