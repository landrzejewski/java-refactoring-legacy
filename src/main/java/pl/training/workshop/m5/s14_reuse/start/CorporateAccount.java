package pl.training.workshop.m5.s14_reuse.start;

/**
 * Start: konto firmowe dziedziczy tylko po to, by nie pisać drugi raz naliczania punktów.
 * Firma zbiera punkty do rocznego rabatu i NIE wymienia ich na bilety - więc odziedziczoną operację
 * blokuje wyjątkiem. Każdy klient LoyaltyAccount może dostać ten obiekt i wybuchnąć.
 */
public class CorporateAccount extends LoyaltyAccount {
    public CorporateAccount(String company) {
        super(company);
    }

    @Override
    public boolean redeemFreeTicket() {
        throw new UnsupportedOperationException("konto firmowe nie wymienia punktów na bilety");
    }
}
