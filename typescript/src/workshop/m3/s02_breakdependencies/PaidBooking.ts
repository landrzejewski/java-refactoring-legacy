import type { LocalDateTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny: opłacona rezerwacja, której może dotyczyć przypomnienie. */
export class PaidBooking {
  constructor(
    readonly id: string,
    readonly email: string,
    readonly title: string,
    readonly start: LocalDateTime,
    readonly reminded: boolean,
  ) {}
}
