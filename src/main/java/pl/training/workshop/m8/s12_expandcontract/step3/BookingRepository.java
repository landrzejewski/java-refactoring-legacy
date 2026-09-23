package pl.training.workshop.m8.s12_expandcontract.step3;

import java.util.Optional;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.m8.s12_expandcontract.BookingTable;

/**
 * Krok 3: backfill - migrateAll uzupełnia payload w starych wierszach (idempotentnie, z licznikiem).
 * Dopiero gdy nie ma wiersza bez payload, można przestać czytać i pisać stary format.
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

    /** Uzupełnia payload tam, gdzie go brakuje; zwraca liczbę zmigrowanych wierszy. */
    public int migrateAll() {
        int migrated = 0;
        for (String id : table.ids()) {
            BookingTable.Row row = table.get(id).orElseThrow();
            if (row.payload() == null) {
                table.put(id, new BookingTable.Row(row.csv(),
                        BookingPayloadFormat.write(CsvBookingFormat.read(row.csv()))));
                migrated++;
            }
        }
        return migrated;
    }

    private static Booking read(BookingTable.Row row) {
        return row.payload() != null
                ? BookingPayloadFormat.read(row.payload())
                : CsvBookingFormat.read(row.csv());
    }
}
