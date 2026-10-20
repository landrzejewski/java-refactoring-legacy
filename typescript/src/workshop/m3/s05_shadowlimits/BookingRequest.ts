/** Stabilny kontrakt sceny: rezerwacja online biletów 2D (25.00 + 2.00 opłaty za bilet). */
export class BookingRequest {
  constructor(
    readonly email: string,
    readonly card: string,
    readonly title: string,
    readonly tickets: number,
  ) {}
}
