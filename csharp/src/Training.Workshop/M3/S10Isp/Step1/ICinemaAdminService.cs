namespace Training.Workshop.M3.S10Isp.Step1;

/// <summary>
/// Krok 1: Extract Interface trzy razy - po jednej roli na klienta. Gruby interfejs
/// na razie zostaje jako suma ról (nic poza klientami się nie psuje).
/// Klienci zależą już tylko od swojej roli.
/// </summary>
public interface ICinemaAdminService : ITicketSales, ISalesFigures, IScreeningSchedule
{
    void UpdateTicketPrice(decimal price);
}
