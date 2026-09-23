package pl.training.workshop.m3.s10_isp.step2;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Krok 2 (rozwiązanie): gruby interfejs usunięty (Safe Delete po przepięciu użyć).
 * Implementacja realizuje trzy role klientów; zmiana ceny to zwykła metoda
 * klasy - jej jedynym klientem jest konfiguracja, więc nie potrzebuje interfejsu.
 */
public final class InMemoryBackOffice implements TicketSales, SalesFigures, ScreeningSchedule {
    private final Map<String, String> activeTickets = new LinkedHashMap<>();
    private final Map<LocalTime, String> schedule = new TreeMap<>();
    private BigDecimal ticketPrice = new BigDecimal("25.00");
    private int nextTicket = 1;

    @Override
    public String sellTicket(String title, int seat) {
        String id = "T-" + nextTicket++;
        activeTickets.put(id, title);
        return id;
    }

    @Override
    public String refundTicket(String ticketId) {
        activeTickets.remove(ticketId);
        return "zwrot " + ticketId;
    }

    @Override
    public BigDecimal dailyRevenue() {
        return ticketPrice.multiply(BigDecimal.valueOf(activeTickets.size()));
    }

    @Override
    public int ticketsSold(String title) {
        return (int) activeTickets.values().stream().filter(title::equals).count();
    }

    @Override
    public void scheduleScreening(String title, LocalTime start) {
        schedule.put(start, title);
    }

    @Override
    public void cancelScreening(String title) {
        schedule.values().remove(title);
    }

    @Override
    public List<String> screenings() {
        List<String> result = new ArrayList<>();
        schedule.forEach((start, title) -> result.add(start + " " + title));
        return result;
    }

    public void updateTicketPrice(BigDecimal price) {
        this.ticketPrice = price;
    }
}
