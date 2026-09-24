/** Stabilny kontrakt sceny: próba rezerwacji z wynikami wcześniejszych sprawdzeń. */
export class BookingAttempt {
  constructor(
    readonly email: string,
    readonly screeningFound: boolean,
    readonly salesOpen: boolean,
    readonly customerBlocked: boolean,
    readonly requestedSeats: number,
    readonly freeSeats: number,
  ) {}
}
