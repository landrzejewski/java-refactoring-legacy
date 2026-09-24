import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Krok 1: Hall implementuje SeatMap. Sala kinowa z miejscami 1..capacity.
 *
 * Kontrakt `reserve`: dla wolnego miejsca z zakresu rezerwuje je - potem
 * `isFree(seat) === false`, a `freeSeats()` maleje o 1. Zajęte miejsce:
 * `IllegalStateError`. Nie ma warunku "ta sala może odmówić".
 */
export class Hall implements SeatMap {
  private readonly capacityValue: number;
  private readonly taken = new Set<number>();

  // Drugi parametr tylko dla podklas (sala z już zajętymi miejscami).
  constructor(capacityValue: number, alreadyTaken: Iterable<number> = []) {
    this.capacityValue = capacityValue;
    for (const seat of alreadyTaken) {
      this.taken.add(seat);
    }
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
