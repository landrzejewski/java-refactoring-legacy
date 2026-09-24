/**
 * Próbka "brudnego" kodu domeny dla bramki: znacznik do zrobienia, wydruk na konsolę,
 * parametry bez typu (niejawne any - odpowiednik surowego typu) i metody bez testu.
 * Plik jest wyłączony z typechecku projektu (tsconfig "exclude") - sprawdza go tylko bramka.
 */
export class PriceTable {
  private readonly lookups: string[] = [];

  basePrice(format: string): number {
    // TODO dodać 4DX
    this.remember(format, 'base');
    switch (format) {
      case 'IMAX': return 40;
      case '3D': return 32;
      default: return 25;
    }
  }

  vipSurcharge(row: number): number {
    console.log('VIP? rzad ' + row);
    return row >= 10 ? 10 : 0;
  }

  lookupCount(): number {
    return this.lookups.length;
  }

  private remember(what, kind) {
    this.lookups.push(kind + ':' + what);
  }
}
