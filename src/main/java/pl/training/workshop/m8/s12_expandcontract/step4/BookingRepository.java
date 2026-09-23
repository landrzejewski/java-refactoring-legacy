package pl.training.workshop.m8.s12_expandcontract.step4;

import java.util.Optional;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.m8.s12_expandcontract.BookingTable;

/**
 * Krok 4: contract - po backfillu i zamknięciu okna wycofania usuwamy stary format (Safe Delete
 * klasy formatu, fallbacku i podwójnego zapisu). Kolejna migracja schematu usunie starą kolumnę.
 */
public final class BookingRepository {
    private final BookingTable table;

    public BookingRepository(BookingTable table) {
        this.table = table;
    }

    public void save(Booking booking) {
        table.put(booking.id(), BookingTable.Row.withPayload(BookingPayloadFormat.write(booking)));
    }

    public Optional<Booking> find(String id) {
        return table.get(id).map(BookingTable.Row::payload).map(BookingPayloadFormat::read);
    }
}
