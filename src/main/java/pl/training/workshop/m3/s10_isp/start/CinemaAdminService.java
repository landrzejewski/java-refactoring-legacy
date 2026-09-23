package pl.training.workshop.m3.s10_isp.start;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Start: gruby interfejs "wszystkiego, co umie zaplecze kina". Każdy klient (kasa,
 * raport, tablica seansów) zależy od ośmiu metod, choć używa dwóch-trzech.
 * Zmiana sygnatury harmonogramu wymusza rekompilację kasy, a fake w teście kasy
 * musi implementować metody raportów i cennika.
 */
public interface CinemaAdminService {
    String sellTicket(String title, int seat);

    String refundTicket(String ticketId);

    BigDecimal dailyRevenue();

    int ticketsSold(String title);

    void scheduleScreening(String title, LocalTime start);

    void cancelScreening(String title);

    List<String> screenings();

    void updateTicketPrice(BigDecimal price);
}
