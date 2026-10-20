/** Krok 1: wynik cennika niesie informację o rabacie - zamiast efektu ubocznego mamy daną. */
export class Quote {
  constructor(readonly total: number, readonly groupDiscount: boolean) {}
}
