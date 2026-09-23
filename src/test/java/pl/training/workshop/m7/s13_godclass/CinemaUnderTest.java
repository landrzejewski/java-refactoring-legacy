package pl.training.workshop.m7.s13_godclass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

/**
 * Publiczne API CinemaManager plus dostęp do globalnego stanu i efektów ubocznych.
 * Każdy wariant sceny ma adapter Driver we własnym pakiecie testowym (dostęp do haka clock).
 * Wszystkie warianty zachowują to samo API - to warunek kampanii Remove God Class.
 */
public interface CinemaUnderTest {
    void reset();

    void clock(Supplier<LocalDateTime> clock);

    void addScreening(String id, String title, int format, LocalDateTime start, int rows, int seatsPerRow,
            int vipFromRow);

    String book(String screeningId, String email, String phone, String[] seats, String[] types, boolean web,
            boolean ownGlasses);

    String pay(String bookingId, String card);

    String cancel(String bookingId);

    void expireOld();

    String use(String bookingId);

    int loyaltyPoints(String email);

    List<String> freeSeats(String screeningId);

    String dailyReport(LocalDate day);

    String settlement(String title, int week);

    List<String> sentMessages();

    List<String> gatewayOperations();
}
