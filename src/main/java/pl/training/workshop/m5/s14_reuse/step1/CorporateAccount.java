package pl.training.workshop.m5.s14_reuse.step1;

/**
 * Krok 1: bez zmian - nadal dziedziczy (i nadal łamie zastępowalność).
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
