/** Krok 1 (bez zmian w kroku 2): pozytywny predykat vip delegujący do starego pola. */
export class Customer {
  constructor(readonly email: string, readonly notVip: boolean) {}

  get vip(): boolean {
    return !this.notVip;
  }
}
