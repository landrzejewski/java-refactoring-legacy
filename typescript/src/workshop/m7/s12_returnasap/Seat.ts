/** Stabilny kontrakt sceny: miejsce w sali. */
export class Seat {
  constructor(readonly label: string, readonly row: number, readonly taken: boolean) {}
}
