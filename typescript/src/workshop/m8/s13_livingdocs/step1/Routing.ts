export class Route {
  constructor(readonly operation: string, readonly target: string) {}
}

/**
 * Krok 1 (bez zmian): routing jako kod - jedyne źródło prawdy dla dokumentu.
 */
export class Routing {
  private constructor() {}

  static routes(): readonly Route[] {
    return Object.freeze([
      new Route('book', 'new'),
      new Route('report', 'new'),
      new Route('cancel', 'legacy'),
    ]);
  }
}
