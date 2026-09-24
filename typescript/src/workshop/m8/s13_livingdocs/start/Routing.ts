export class Route {
  constructor(readonly operation: string, readonly target: string) {}
}

/**
 * Start: routing fasady Strangler Fig jako kod - źródło prawdy. Obok leży ROUTING.md pisany
 * ręcznie: raport przejęto miesiąc temu, anulowanie dodano tydzień temu, a dokument o tym nie wie.
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
