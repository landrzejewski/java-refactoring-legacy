using System.Globalization;

namespace Training.Workshop.M3.S10Isp.Start;

/// <summary>Klient: raport. Używa DailyRevenue i TicketsSold.</summary>
public sealed class RevenueReport
{
    private readonly ICinemaAdminService _backOffice;

    public RevenueReport(ICinemaAdminService backOffice)
    {
        _backOffice = backOffice;
    }

    public string Summary(string title)
    {
        return title + ": " + _backOffice.TicketsSold(title) + " biletow, dzien: "
            + _backOffice.DailyRevenue().ToString(CultureInfo.InvariantCulture);
    }
}
