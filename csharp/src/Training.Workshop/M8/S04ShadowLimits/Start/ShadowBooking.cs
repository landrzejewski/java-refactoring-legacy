namespace Training.Workshop.M8.S04ShadowLimits.Start;

/// <summary>
/// Start: tryb shadow "jak dla kalkulatora" zastosowany do ścieżki z efektami ubocznymi.
/// Wynik jest z legacy, ale kandydat też obciąża kartę, zapisuje i wysyła mail -
/// klient dostaje dwa maile i dwa obciążenia. Porównujemy tylko zwracany tekst.
/// </summary>
public sealed class ShadowBooking
{
    private readonly LegacyBookingFlow _legacy;
    private readonly NewBookingFlow _candidate;
    private readonly List<string> _divergences = [];

    public ShadowBooking(Infrastructure infra)
    {
        _legacy = new LegacyBookingFlow(infra);
        _candidate = new NewBookingFlow(infra);
    }

    public string Book(BookingRequest request)
    {
        string result = _legacy.Book(request);
        try
        {
            string shadow = _candidate.Book(request);
            if (!shadow.Equals(result))
            {
                _divergences.Add("legacy: " + result + " | kandydat: " + shadow);
            }
        }
        catch (Exception failure)
        {
            _divergences.Add("kandydat: " + failure.Message);
        }
        return result;
    }

    public IReadOnlyList<string> Divergences()
    {
        return _divergences.ToList();
    }
}
