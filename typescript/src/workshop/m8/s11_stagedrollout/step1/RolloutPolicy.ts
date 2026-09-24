/**
 * Krok 1: polityka jako wartość - Introduce Parameter Object dla flagi i listy wyjątków.
 * Konfigurację da się teraz podać z zewnątrz, przetestować i opisać w przeglądzie.
 */
export class RolloutPolicy {
  readonly allowList: ReadonlySet<string>;

  constructor(readonly enabled: boolean, allowList: Iterable<string>) {
    this.allowList = new Set(allowList);
  }

  /** Dotychczasowe ustawienia produkcyjne - te same, co stała i if w starym kodzie. */
  static current(): RolloutPolicy {
    return new RolloutPolicy(false, ['anna@kino.pl', 'jan@kino.pl']);
  }

  allows(email: string): boolean {
    return this.enabled || this.allowList.has(email);
  }
}
