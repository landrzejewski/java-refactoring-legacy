using System.Globalization;

namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): gruby interfejs usunięty (Safe Delete po przepięciu użyć).
/// Implementacja realizuje trzy role klientów; zmiana ceny to zwykła metoda
/// klasy - jej jedynym klientem jest konfiguracja, więc nie potrzebuje interfejsu.
/// </summary>
public sealed class InMemoryBackOffice : ITicketSales, ISalesFigures, IScreeningSchedule
{
    private readonly Dictionary<string, string> _activeTickets = [];
    private readonly SortedDictionary<TimeOnly, string> _schedule = [];
    private decimal _ticketPrice = 25.00m;
    private int _nextTicket = 1;

    public string SellTicket(string title, int seat)
    {
        var id = "T-" + _nextTicket++;
        _activeTickets[id] = title;
        return id;
    }

    public string RefundTicket(string ticketId)
    {
        _activeTickets.Remove(ticketId);
        return "zwrot " + ticketId;
    }

    public decimal DailyRevenue()
    {
        return _ticketPrice * _activeTickets.Count;
    }

    public int TicketsSold(string title)
    {
        return _activeTickets.Values.Count(title.Equals);
    }

    public void ScheduleScreening(string title, TimeOnly start)
    {
        _schedule[start] = title;
    }

    public void CancelScreening(string title)
    {
        foreach (var (start, scheduled) in _schedule)
        {
            if (scheduled == title)
            {
                _schedule.Remove(start);
                return;
            }
        }
    }

    public IReadOnlyList<string> Screenings()
    {
        var result = new List<string>();
        foreach (var (start, title) in _schedule)
        {
            result.Add(start.ToString("HH:mm", CultureInfo.InvariantCulture) + " " + title);
        }
        return result;
    }

    public void UpdateTicketPrice(decimal price)
    {
        _ticketPrice = price;
    }
}
