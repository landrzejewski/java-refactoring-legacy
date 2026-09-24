import type { Booking } from '../Booking.js';
import { type BookingTable, Row } from '../BookingTable.js';
import { CsvBookingFormat } from './CsvBookingFormat.js';

/**
 * Start: repozytorium zna tylko kolumnę csv. Zmiana formatu "w miejscu" (zapis nowego formatu
 * do tej samej kolumny) uniemożliwiłaby wycofanie wydania - stara wersja nie odczyta danych.
 */
export class BookingRepository {
  constructor(private readonly table: BookingTable) {}

  save(booking: Booking): void {
    this.table.put(booking.id, new Row(CsvBookingFormat.write(booking), null));
  }

  find(id: string): Booking | undefined {
    const csv = this.table.get(id)?.csv;
    return csv == null ? undefined : CsvBookingFormat.read(csv);
  }
}
