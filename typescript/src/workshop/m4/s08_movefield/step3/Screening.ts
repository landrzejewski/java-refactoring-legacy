import type { Hall } from './Hall.js';

/**
 * Krok 3 (rozwiązanie): aktualizacja odczytów. Reguła "rząd >= próg" trafiła do Hall.isVip,
 * SeatPricer pyta o VIP-owość zamiast czytać surowy próg, a przejściowy getter
 * `Screening.vipFromRow` został usunięty (Safe Delete). Jedno źródło prawdy.
 */
export class Screening {
  constructor(
    readonly hall: Hall,
    readonly format: number,
  ) {}

  isVip(row: number): boolean {
    return this.hall.isVip(row);
  }
}
