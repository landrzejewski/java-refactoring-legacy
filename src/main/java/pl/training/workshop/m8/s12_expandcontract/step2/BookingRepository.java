package pl.training.workshop.m8.s12_expandcontract.step2;

import java.util.Optional;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.m8.s12_expandcontract.BookingTable;

/**
 * Krok 2: odczyt z nowej kolumny z fallbackiem - wiersze sprzed kroku 1 mają tylko csv.
 * Zapis nadal podwójny, więc wycofanie do kroku 1 albo startu wciąż jest możliwe.
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
        return table.get(id).map(BookingRepository::read);
    }

    private static Booking read(BookingTable.Row row) {
        return row.payload() != null
                ? BookingPayloadFormat.read(row.payload())
                : CsvBookingFormat.read(row.csv());
    }
}
