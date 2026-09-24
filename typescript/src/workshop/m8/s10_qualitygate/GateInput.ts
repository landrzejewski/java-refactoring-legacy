/**
 * Zestaw testów uruchamiany przez bramkę: nazwa (jak prosta nazwa klasy testowej)
 * i przypadki testowe - każdy przypadek przechodzi albo rzuca wyjątek.
 */
export interface TestSuite {
  readonly name: string;
  readonly tests: Readonly<Record<string, () => void>>;
}

/**
 * Stabilny kontrakt sceny: co bramka ma sprawdzić.
 *
 * sources: katalog ze źródłami domeny (bez podkatalogów)
 * testSource: plik testu kluczowej klasy
 * keyClass: nazwa kluczowej klasy domeny, np. PriceTable
 * testSuite: załadowany zestaw testów do uruchomienia (w Javie: pełna nazwa klasy testowej)
 */
export class GateInput {
  constructor(
    readonly sources: string,
    readonly testSource: string,
    readonly keyClass: string,
    readonly testSuite: TestSuite,
  ) {}
}
