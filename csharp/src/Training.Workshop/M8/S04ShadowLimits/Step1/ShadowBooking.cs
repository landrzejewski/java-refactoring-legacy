namespace Training.Workshop.M8.S04ShadowLimits.Step1;

/// <summary>
/// Krok 1: cień nadal podaje kandydatowi prawdziwe efekty - podwójne maile i obciążenia zostają.
/// Ale mamy już szew: w następnym kroku wystarczy podać inną implementację IEffects.
/// </summary>
public sealed class ShadowBooking
{
    private readonly LegacyBookingFlow _legacy;
    private readonly NewBookingFlow _candidate;
    private readonly List<string> _divergences = [];

    public ShadowBooking(Infrastructure infra)
    {
        _legacy = new LegacyBookingFlow(infra);
        _candidate = new NewBookingFlow(new RealEffects(infra));
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
