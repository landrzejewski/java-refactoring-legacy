package pl.training.workshop.m8.s12_expandcontract.start;

import java.util.Optional;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.m8.s12_expandcontract.BookingTable;

/**
 * Start: repozytorium zna tylko kolumnę csv. Zmiana formatu "w miejscu" (zapis nowego formatu
 * do tej samej kolumny) uniemożliwiłaby wycofanie wydania - stara wersja nie odczyta danych.
 */
public final class BookingRepository {
    private final BookingTable table;

    public BookingRepository(BookingTable table) {
        this.table = table;
    }

    public void save(Booking booking) {
        table.put(booking.id(), new BookingTable.Row(CsvBookingFormat.write(booking), null));
    }

    public Optional<Booking> find(String id) {
        return table.get(id).map(BookingTable.Row::csv).map(CsvBookingFormat::read);
    }
}
