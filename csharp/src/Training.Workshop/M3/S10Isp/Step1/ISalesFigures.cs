namespace Training.Workshop.M3.S10Isp.Step1;

/// <summary>Krok 1: rola z perspektywy raportu.</summary>
public interface ISalesFigures
{
    decimal DailyRevenue();

    int TicketsSold(string title);
}
