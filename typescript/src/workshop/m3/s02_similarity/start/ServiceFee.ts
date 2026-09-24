import { Decimal } from 'decimal.js';

export enum Kind {
  ONLINE_BOOKING = 'ONLINE_BOOKING',
  REFUND = 'REFUND',
}

/**
 * Start: "zDRYowana" opłata. Ktoś zauważył, że opłata rezerwacyjna online (2.00 za bilet)
 * i potrącenie przy zwrocie (3.00) wyglądają tak samo: "stała kwota razy liczba sztuk".
 * Powstała wspólna metoda z przełącznikiem, czyli fałszywa zależność między regułami
 * dwóch różnych właścicieli: sprzedaży online (marketing) i regulaminu zwrotów (obsługa klienta).
 */
export class ServiceFee {
  private constructor() {}

  static of(kind: Kind, units: number): Decimal {
    const perUnit = kind === Kind.ONLINE_BOOKING ? new Decimal('2.00') : new Decimal('3.00');
    return perUnit.times(units).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
