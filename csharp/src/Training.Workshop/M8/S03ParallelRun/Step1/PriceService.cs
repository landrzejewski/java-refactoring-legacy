using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Step1;

/// <summary>
/// Krok 1: dodanie cienia. Kandydat liczy na tym samym wejściu, ale klient zawsze dostaje
/// wynik legacy. Awaria kandydata jest izolowana (try/catch), a nie przerywa sprzedaży.
/// </summary>
public sealed class PriceService
{
    private readonly LegacyPriceCalculator _legacy = new();
    private readonly CandidatePriceCalculator _candidate = new();
    private int _mismatches;

    public Money Price(TicketQuery query)
    {
        Money result = _legacy.Price(query);
        try
        {
            if (!_candidate.Price(query).Equals(result))
            {
                _mismatches++;
            }
        }
        catch (Exception)
        {
            _mismatches++;
        }
        return result;
    }

    /// <summary>Wiemy, ŻE coś się różni, ale nie wiemy CO - licznik to za mało do decyzji.</summary>
    public int Mismatches => _mismatches;
}
