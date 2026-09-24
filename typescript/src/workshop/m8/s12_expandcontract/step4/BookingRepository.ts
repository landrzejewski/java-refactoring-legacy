import type { Booking } from '../Booking.js';
import { type BookingTable, Row } from '../BookingTable.js';
import { BookingPayloadFormat } from './BookingPayloadFormat.js';

/**
 * Krok 4: contract - po backfillu i zamknięciu okna wycofania usuwamy stary format (Safe Delete
 * klasy formatu, fallbacku i podwójnego zapisu). Kolejna migracja schematu usunie starą kolumnę.
 */
export class BookingRepository {
  constructor(private readonly table: BookingTable) {}

  save(booking: Booking): void {
    this.table.put(booking.id, Row.withPayload(BookingPayloadFormat.write(booking)));
  }

  find(id: string): Booking | undefined {
    const payload = this.table.get(id)?.payload;
    return payload == null ? undefined : BookingPayloadFormat.read(payload);
  }
}
