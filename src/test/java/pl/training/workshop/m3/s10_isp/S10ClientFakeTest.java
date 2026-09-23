package pl.training.workshop.m3.s10_isp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ISP widziany z testu klienta: ile trzeba zaimplementować, żeby przetestować kasę?
 * Gruby CinemaAdminService (jak w start; tu z kroku 1, bo start jest edytowany na żywo)
 * - osiem metod, sześć "nie dotyczy". Rola TicketSales - dwie.
 */
final class S10ClientFakeTest {
    @Test
    void fatInterfaceForcesAFakeOfTheWholeBackOffice() {
        var fake = new pl.training.workshop.m3.s10_isp.step1.CinemaAdminService() {
            public String sellTicket(String title, int seat) { return "T-9"; }
            public String refundTicket(String ticketId) { return "zwrot " + ticketId; }
            public BigDecimal dailyRevenue() { throw new UnsupportedOperationException(); }
            public int ticketsSold(String title) { throw new UnsupportedOperationException(); }
            public void scheduleScreening(String title, LocalTime start) { throw new UnsupportedOperationException(); }
            public void cancelScreening(String title) { throw new UnsupportedOperationException(); }
            public List<String> screenings() { throw new UnsupportedOperationException(); }
            public void updateTicketPrice(BigDecimal price) { throw new UnsupportedOperationException(); }
        };
        assertEquals("bilet T-9: Amator, miejsce 4",
                new pl.training.workshop.m3.s10_isp.step1.CashDesk(fake).sell("Amator", 4));
    }

    @Test
    void step2CashDeskNeedsOnlyItsRole() {
        var fake = new pl.training.workshop.m3.s10_isp.step2.TicketSales() {
            public String sellTicket(String title, int seat) { return "T-9"; }
            public String refundTicket(String ticketId) { return "zwrot " + ticketId; }
        };
        assertEquals("bilet T-9: Amator, miejsce 4",
                new pl.training.workshop.m3.s10_isp.step2.CashDesk(fake).sell("Amator", 4));
    }
}
