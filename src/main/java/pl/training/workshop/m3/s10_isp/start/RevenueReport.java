package pl.training.workshop.m3.s10_isp.start;

/** Klient: raport. Używa dailyRevenue i ticketsSold. */
public final class RevenueReport {
    private final CinemaAdminService backOffice;

    public RevenueReport(CinemaAdminService backOffice) {
        this.backOffice = backOffice;
    }

    public String summary(String title) {
        return title + ": " + backOffice.ticketsSold(title) + " biletow, dzien: "
                + backOffice.dailyRevenue();
    }
}
