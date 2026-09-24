namespace Training.Workshop.M3.S10Isp.Start;

/// <summary>
/// Start: gruby interfejs "wszystkiego, co umie zaplecze kina". Każdy klient (kasa,
/// raport, tablica seansów) zależy od ośmiu metod, choć używa dwóch-trzech.
/// Zmiana sygnatury harmonogramu wymusza rekompilację kasy, a fake w teście kasy
/// musi implementować metody raportów i cennika.
/// </summary>
public interface ICinemaAdminService
{
    string SellTicket(string title, int seat);

    string RefundTicket(string ticketId);

    decimal DailyRevenue();

    int TicketsSold(string title);

    void ScheduleScreening(string title, TimeOnly start);

    void CancelScreening(string title);

    IReadOnlyList<string> Screenings();

    void UpdateTicketPrice(decimal price);
}
