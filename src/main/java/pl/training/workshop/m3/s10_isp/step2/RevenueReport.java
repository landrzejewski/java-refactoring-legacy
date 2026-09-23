package pl.training.workshop.m3.s10_isp.step2;

/** Raport zależy tylko od roli SalesFigures. */
public final class RevenueReport {
    private final SalesFigures backOffice;

    public RevenueReport(SalesFigures backOffice) {
        this.backOffice = backOffice;
    }

    public String summary(String title) {
        return title + ": " + backOffice.ticketsSold(title) + " biletow, dzien: "
                + backOffice.dailyRevenue();
    }
}
