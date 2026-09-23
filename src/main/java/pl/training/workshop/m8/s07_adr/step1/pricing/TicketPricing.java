package pl.training.workshop.m8.s07_adr.step1.pricing;

/**
 * Krok 1: spełnienie R1 - cennik nie zna powiadomień. Zwraca Quote z flagą rabatu,
 * a decyzję o mailu podejmuje BookingService. R2 (double) nadal naruszona.
 */
public final class TicketPricing {
    public Quote total(int tickets, double unitPrice) {
        double sum = unitPrice * tickets;
        boolean groupDiscount = tickets >= 10;
        if (groupDiscount) {
            sum = sum - sum * 0.10;
        }
        return new Quote(Math.round(sum * 100) / 100.0, groupDiscount);
    }
}
