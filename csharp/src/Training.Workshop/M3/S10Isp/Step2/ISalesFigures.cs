namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>Rola z perspektywy raportu.</summary>
public interface ISalesFigures
{
    decimal DailyRevenue();

    int TicketsSold(string title);
}
