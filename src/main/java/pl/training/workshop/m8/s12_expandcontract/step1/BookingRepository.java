package pl.training.workshop.m8.s12_expandcontract.step1;

import java.util.Optional;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.m8.s12_expandcontract.BookingTable;

/**
 * Krok 1: expand + dual write - zapis do obu kolumn, odczyt nadal ze starej. Wycofanie do
 * poprzedniej wersji jest bezpieczne: stara wersja czyta csv, który wciąż powstaje.
 */
public final class BookingRepository {
    private final BookingTable table;

    public BookingRepository(BookingTable table) {
        this.table = table;
    }

    public void save(Booking booking) {
        table.put(booking.id(), new BookingTable.Row(CsvBookingFormat.write(booking),
                BookingPayloadFormat.write(booking)));
    }

    public Optional<Booking> find(String id) {
        return table.get(id).map(BookingTable.Row::csv).map(CsvBookingFormat::read);
    }
}
