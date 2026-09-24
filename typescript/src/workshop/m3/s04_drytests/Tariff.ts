import { Decimal } from 'decimal.js';

/**
 * Kod produkcyjny sceny (stabilny): taryfa - ceny formatów i zniżki typów biletów.
 * `withDiscount` pozwala testom podłożyć taryfę z błędem.
 */
export class Tariff {
  readonly basePrices: ReadonlyMap<string, Decimal>;
  readonly discountPercents: ReadonlyMap<string, number>;

  constructor(basePrices: ReadonlyMap<string, Decimal>, discountPercents: ReadonlyMap<string, number>) {
    this.basePrices = new Map(basePrices);
    this.discountPercents = new Map(discountPercents);
  }

  static standard(): Tariff {
    return new Tariff(
      new Map([
        ['2D', new Decimal('25.00')],
        ['3D', new Decimal('32.00')],
        ['IMAX', new Decimal('40.00')],
      ]),
      new Map([['NORMAL', 0], ['STUDENT', 25], ['SENIOR', 30], ['CHILD', 40]]));
  }

  withDiscount(type: string, percent: number): Tariff {
    const changed = new Map(this.discountPercents);
    changed.set(type, percent);
    return new Tariff(this.basePrices, changed);
  }
}
