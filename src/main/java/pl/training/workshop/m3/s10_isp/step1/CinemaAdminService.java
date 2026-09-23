package pl.training.workshop.m3.s10_isp.step1;

import java.math.BigDecimal;

/**
 * Krok 1: Extract Interface trzy razy - po jednej roli na klienta. Gruby interfejs
 * na razie zostaje jako suma ról (nic poza klientami się nie psuje).
 * Klienci zależą już tylko od swojej roli.
 */
public interface CinemaAdminService extends TicketSales, SalesFigures, ScreeningSchedule {
    void updateTicketPrice(BigDecimal price);
}
