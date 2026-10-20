/** Stabilny kontrakt sceny: seans w katalogu. */
export class Screening {
  constructor(
    readonly id: string,
    readonly title: string,
    readonly format: string,
    readonly freeSeats: number,
  ) {}
}
