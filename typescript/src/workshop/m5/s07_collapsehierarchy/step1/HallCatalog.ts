import { Hall } from './Hall.js';
import { ImaxHall } from './ImaxHall.js';

/** Krok 1: bez zmian. */
export class HallCatalog {
  readonly #halls: readonly Hall[] = Object.freeze([
    new Hall('Sala 1', 12, 15, 10),
    new Hall('Sala 2', 10, 12, 9),
    new ImaxHall('Sala IMAX', 14, 22),
  ]);

  describe(name: string): string {
    return this.#halls
      .filter((hall) => hall.name() === name)
      .map((hall) => hall.describe())
      .at(0) ?? 'brak sali: ' + name;
  }

  isVip(name: string, row: number): boolean {
    return this.#halls.some((hall) => hall.name() === name && hall.isVip(row));
  }
}
