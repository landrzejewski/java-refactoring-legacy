using System.Globalization;

namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>Raport zależy tylko od roli ISalesFigures.</summary>
public sealed class RevenueReport
{
    private readonly ISalesFigures _backOffice;

    public RevenueReport(ISalesFigures backOffice)
    {
        _backOffice = backOffice;
    }

    public string Summary(string title)
    {
        return title + ": " + _backOffice.TicketsSold(title) + " biletow, dzien: "
            + _backOffice.DailyRevenue().ToString(CultureInfo.InvariantCulture);
    }
}
