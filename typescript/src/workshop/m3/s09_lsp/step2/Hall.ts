import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Krok 2: Hall jest "final" - nikt już nie dziedziczy, żeby "wyłączyć" rezerwację
 * (TypeScript nie ma final, więc to decyzja zapisana w dokumentacji klasy).
 * Parametr konstruktora dla podklasy zniknął.
 *
 * Kontrakt `reserve`: dla wolnego miejsca z zakresu rezerwuje je - potem
 * `isFree(seat) === false`, a `freeSeats()` maleje o 1. Zajęte miejsce:
 * `IllegalStateError`.
 *
 * @sealed
 */
export class Hall implements SeatMap {
  private readonly capacityValue: number;
  private readonly taken = new Set<number>();

  constructor(capacityValue: number) {
    this.capacityValue = capacityValue;
  }

  reserve(seat: number): void {
    if (seat < 1 || seat > this.capacityValue) {
      throw new IllegalArgumentError(`brak miejsca ${seat}`);
    }
    if (this.taken.has(seat)) {
      throw new IllegalStateError(`miejsce zajete: ${seat}`);
    }
    this.taken.add(seat);
  }

  isFree(seat: number): boolean {
    return seat >= 1 && seat <= this.capacityValue && !this.taken.has(seat);
  }

  freeSeats(): number {
    return this.capacityValue - this.taken.size;
  }

  capacity(): number {
    return this.capacityValue;
  }
}
