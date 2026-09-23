package pl.training.workshop.m4.s06_inlinemethod.start;

import java.math.BigDecimal;

/** Sprzedaż internetowa: ta sama cena biletu, plus opłata rezerwacyjna 2.00. */
public final class OnlineTicketPricing extends TicketPricing {
    @Override
    protected BigDecimal bookingFee() {
        return new BigDecimal("2.00");
    }
}
