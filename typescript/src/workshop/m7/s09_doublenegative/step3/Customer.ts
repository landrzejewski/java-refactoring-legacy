/**
 * Krok 3: odwrócenie delegacji - pole nazywa się vip, negatyw zniknął.
 * Uwaga: zmienia się znaczenie argumentu konstruktora (true = VIP),
 * a gdyby obiekt trafiał do JSON-a lub bazy, zmieniłaby się też granica - to wymaga migracji.
 */
export class Customer {
  constructor(readonly email: string, readonly vip: boolean) {}
}
