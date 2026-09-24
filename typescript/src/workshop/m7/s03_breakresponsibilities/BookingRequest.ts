/**
 * Stabilny kontrakt sceny: prośba o rezerwację.
 *
 * seats - miejsca w postaci litera + rząd, np. C10 (rząd 10 i dalej to VIP)
 */
export class BookingRequest {
  constructor(
    readonly email: string | null,
    readonly format: string,
    readonly seats: readonly string[],
  ) {}
}
