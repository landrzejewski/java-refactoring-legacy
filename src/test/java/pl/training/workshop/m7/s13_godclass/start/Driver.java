package pl.training.workshop.m7.s13_godclass.start;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import pl.training.workshop.m7.s13_godclass.CinemaUnderTest;

/** Adapter wariantu start dla S13Script (ten sam pakiet - dostęp do haka CinemaManager.clock). */
public final class Driver implements CinemaUnderTest {
    private CinemaManager cinema = new CinemaManager();

    @Override
    public void reset() {
        LegacyDb.clear();
        cinema = new CinemaManager();
    }

    @Override
    public void clock(Supplier<LocalDateTime> clock) {
        CinemaManager.clock = clock;
    }

    @Override
    public void addScreening(String id, String title, int format, LocalDateTime start, int rows, int seatsPerRow,
            int vipFromRow) {
        cinema.addScreening(id, title, format, start, rows, seatsPerRow, vipFromRow);
    }

    @Override
    public String book(String screeningId, String email, String phone, String[] seats, String[] types, boolean web,
            boolean ownGlasses) {
        return cinema.book(screeningId, email, phone, seats, types, web, ownGlasses);
    }

    @Override
    public String pay(String bookingId, String card) {
        return cinema.pay(bookingId, card);
    }

    @Override
    public String cancel(String bookingId) {
        return cinema.cancel(bookingId);
    }

    @Override
    public void expireOld() {
        cinema.expireOld();
    }

    @Override
    public String use(String bookingId) {
        return cinema.use(bookingId);
    }

    @Override
    public int loyaltyPoints(String email) {
        return cinema.loyaltyPoints(email);
    }

    @Override
    public List<String> freeSeats(String screeningId) {
        return cinema.freeSeats(screeningId);
    }

    @Override
    public String dailyReport(LocalDate day) {
        return cinema.dailyReport(day);
    }

    @Override
    public String settlement(String title, int week) {
        return cinema.settlement(title, week);
    }

    @Override
    public List<String> sentMessages() {
        return List.copyOf(LegacyMailer.SENT);
    }

    @Override
    public List<String> gatewayOperations() {
        return List.copyOf(LegacyPaymentGateway.CHARGES);
    }
}
