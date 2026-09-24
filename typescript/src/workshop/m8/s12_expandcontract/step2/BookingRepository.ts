import type { Booking } from '../Booking.js';
import { type BookingTable, Row } from '../BookingTable.js';
import { BookingPayloadFormat } from './BookingPayloadFormat.js';
import { CsvBookingFormat } from './CsvBookingFormat.js';

/**
 * Krok 2: odczyt z nowej kolumny z fallbackiem - wiersze sprzed kroku 1 mają tylko csv.
 * Zapis nadal podwójny, więc wycofanie do kroku 1 albo startu wciąż jest możliwe.
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

  private static read(row: Row): Booking {
    return row.payload != null
      ? BookingPayloadFormat.read(row.payload)
      : CsvBookingFormat.read(row.csv!);
  }
}
