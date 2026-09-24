/** Stabilny kontrakt sceny: bilet z legacy kodem typu ("N", "S", "E", "C") i rzędem. */
export class Ticket {
  constructor(readonly type: string, readonly row: number) {}
}
