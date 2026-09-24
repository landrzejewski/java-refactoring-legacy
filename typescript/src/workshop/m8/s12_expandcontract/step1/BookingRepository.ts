import type { Booking } from '../Booking.js';
import { type BookingTable, Row } from '../BookingTable.js';
import { BookingPayloadFormat } from './BookingPayloadFormat.js';
import { CsvBookingFormat } from './CsvBookingFormat.js';

/**
 * Krok 1: expand + dual write - zapis do obu kolumn, odczyt nadal ze starej. Wycofanie do
 * poprzedniej wersji jest bezpieczne: stara wersja czyta csv, który wciąż powstaje.
 */
export class BookingRepository {
  constructor(private readonly table: BookingTable) {}

  save(booking: Booking): void {
    this.table.put(booking.id, new Row(CsvBookingFormat.write(booking),
      BookingPayloadFormat.write(booking)));
  }

  find(id: string): Booking | undefined {
    const csv = this.table.get(id)?.csv;
    return csv == null ? undefined : CsvBookingFormat.read(csv);
  }
}
