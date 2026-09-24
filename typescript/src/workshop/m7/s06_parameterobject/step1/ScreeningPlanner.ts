import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { LocalDate } from '../../../shared/time.js';
import { ScreeningSlot } from './ScreeningSlot.js';

/**
 * Krok 1: Introduce Parameter Object - nowe metody przyjmują ScreeningSlot, stare sygnatury
 * zostają jako przestarzałe i delegują (okres migracji). Walidacja nadal tu, bez zmian.
 * Java ma tu przeciążenia; w TS to sygnatury przeciążeń z jedną implementacją - stara
 * sygnatura oznaczona @deprecated (edytor ją przekreśla), kształt wywołań się nie zmienia.
 */
export class ScreeningPlanner {
  describe(slot: ScreeningSlot): string;
  /** @deprecated użyj {@link ScreeningPlanner.describe} z ScreeningSlot */
  describe(screeningId: string, date: LocalDate, hall: number, format: string): string;
  describe(slotOrId: ScreeningSlot | string, date?: LocalDate, hall?: number, format?: string): string {
    if (typeof slotOrId === 'string') {
      return this.describe(new ScreeningSlot(slotOrId, date!, hall!, format!));
    }
    const slot = slotOrId;
    return `${slot.screeningId} ${slot.date.toString()} sala ${slot.hall}`
      + ` (${slot.format})`;
  }

  ticketPrice(slot: ScreeningSlot): Decimal;
  /** @deprecated użyj {@link ScreeningPlanner.ticketPrice} z ScreeningSlot */
  ticketPrice(screeningId: string, date: LocalDate, hall: number, format: string): Decimal;
  ticketPrice(slotOrId: ScreeningSlot | string, date?: LocalDate, hall?: number, format?: string): Decimal {
    if (typeof slotOrId === 'string') {
      return this.ticketPrice(new ScreeningSlot(slotOrId, date!, hall!, format!));
    }
    const slot = slotOrId;
    if (slot.hall < 1 || slot.hall > 8) {
      throw new IllegalArgumentError(
        `nie ma sali ${slot.hall} (${slot.screeningId})`);
    }
    switch (slot.format) {
      case '2D': return new Decimal('25.00');
      case '3D': return new Decimal('32.00');
      case 'IMAX': return new Decimal('40.00');
      default: throw new IllegalArgumentError(
        `nieznany format ${slot.format} (${slot.screeningId})`);
    }
  }
}
