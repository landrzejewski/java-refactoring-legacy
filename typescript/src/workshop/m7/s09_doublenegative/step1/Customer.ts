/**
 * Krok 1: pozytywny predykat vip delegujący do starego pola - dokładne dopełnienie.
 * Getter, żeby w kroku 3 pole vip mogło go zastąpić bez zmiany wywołań.
 */
export class Customer {
  constructor(readonly email: string, readonly notVip: boolean) {}

  get vip(): boolean {
    return !this.notVip;
  }
}
