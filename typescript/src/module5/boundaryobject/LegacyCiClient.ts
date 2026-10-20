/**
 * Zewnętrzny system CI: zwraca surowy, nietypowany ładunek
 * (klucze id, status, coverage, failed, commit, wszystkie wartości jako tekst).
 * Brak buildu: undefined (w Javie null).
 */
export interface LegacyCiClient {
  fetchBuild(buildId: string): ReadonlyMap<string, string> | undefined;
}
