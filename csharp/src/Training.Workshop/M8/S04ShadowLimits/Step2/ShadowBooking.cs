namespace Training.Workshop.M8.S04ShadowLimits.Step2;

/// <summary>
/// Krok 2: w cieniu kandydat dostaje RecordingEffects. Klient dostaje jeden mail i jedno
/// obciążenie, a cień porównuje nie tylko wynik, ale też ZAMIERZONE efekty z tymi, które
/// faktycznie wykonało legacy - i znajduje różnicę w treści maila.
/// </summary>
public sealed class ShadowBooking
{
    private readonly Infrastructure _infra;
    private readonly LegacyBookingFlow _legacy;
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
            var recorder = new RecordingEffects();
            string shadow = new NewBookingFlow(recorder).Book(request);
            Compare("wynik", [result], [shadow]);
            Compare("efekty", legacyEffects, recorder.Recorded());
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
