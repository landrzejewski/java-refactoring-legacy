namespace Training.Workshop.M8.S04ShadowLimits.Step3;

/// <summary>
/// Krok 3: cień woła tylko czysty BookingPlanner - bez nagrywarek i bez ryzyka, że ktoś
/// podepnie prawdziwy adapter. Podwójne wykonanie dotyczy wyłącznie obliczenia.
/// </summary>
public sealed class ShadowBooking
{
    private readonly Infrastructure _infra;
    private readonly LegacyBookingFlow _legacy;
    private readonly BookingPlanner _candidate = new();
    private readonly List<string> _divergences = [];

    public ShadowBooking(Infrastructure infra)
    {
        _infra = infra;
        _legacy = new LegacyBookingFlow(infra);
    }

    public string Book(BookingRequest request)
    {
        int before = _infra.Log().Count;
        string result = _legacy.Book(request);
        IReadOnlyList<string> legacyEffects = _infra.Log().Skip(before).ToList();
        try
        {
            BookingPlan plan = _candidate.Plan(request);
            Compare("wynik", [result], [plan.Result]);
            IReadOnlyList<string> planned = plan.Effects.Select(effect => effect.Describe()).ToList();
            Compare("efekty", legacyEffects, planned);
        }
        catch (Exception failure)
        {
            _divergences.Add("kandydat: " + failure.Message);
        }
        return result;
    }

    private void Compare(string what, IReadOnlyList<string> legacyValues, IReadOnlyList<string> candidateValues)
    {
        for (int i = 0; i < Math.Max(legacyValues.Count, candidateValues.Count); i++)
        {
            string legacyValue = i < legacyValues.Count ? legacyValues[i] : "-";
            string candidateValue = i < candidateValues.Count ? candidateValues[i] : "-";
            if (!legacyValue.Equals(candidateValue))
            {
                _divergences.Add(what + " legacy: " + legacyValue + " | kandydat: " + candidateValue);
            }
        }
    }

    public IReadOnlyList<string> Divergences()
    {
        return _divergences.ToList();
    }
}
