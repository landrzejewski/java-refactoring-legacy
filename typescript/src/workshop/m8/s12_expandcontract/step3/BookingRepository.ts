import type { Booking } from '../Booking.js';
import { type BookingTable, Row } from '../BookingTable.js';
import { BookingPayloadFormat } from './BookingPayloadFormat.js';
import { CsvBookingFormat } from './CsvBookingFormat.js';

/**
 * Krok 3: backfill - migrateAll uzupełnia payload w starych wierszach (idempotentnie, z licznikiem).
 * Dopiero gdy nie ma wiersza bez payload, można przestać czytać i pisać stary format.
 */
export class BookingRepository {
  constructor(private readonly table: BookingTable) {}

  save(booking: Booking): void {
    this.table.put(booking.id, new Row(CsvBookingFormat.write(booking),
      BookingPayloadFormat.write(booking)));
  }

  find(id: string): Booking | undefined {
    const row = this.table.get(id);
    return row === undefined ? undefined : BookingRepository.read(row);
  }

  /** Uzupełnia payload tam, gdzie go brakuje; zwraca liczbę zmigrowanych wierszy. */
  migrateAll(): number {
    let migrated = 0;
    for (const id of this.table.ids()) {
      const row = this.table.get(id)!;
      if (row.payload == null) {
        this.table.put(id, new Row(row.csv,
          BookingPayloadFormat.write(CsvBookingFormat.read(row.csv!))));
        migrated++;
      }
    }
    return migrated;
  }

  private static read(row: Row): Booking {
    return row.payload != null
      ? BookingPayloadFormat.read(row.payload)
      : CsvBookingFormat.read(row.csv!);
  }
}
