using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Step2;

/// <summary>
/// Krok 2: wynik porównania trafia do VerificationReport (co, dla jakiego wejścia, ile).
/// Klient nadal dostaje wynik legacy - raport jest dowodem do decyzji o przełączeniu.
/// </summary>
public sealed class PriceService
{
    private readonly LegacyPriceCalculator _legacy = new();
    private readonly CandidatePriceCalculator _candidate = new();
    private readonly VerificationReport _report;

    public PriceService()
        : this(new VerificationReport())
    {
    }

    public PriceService(VerificationReport report)
    {
        _report = report;
    }

    public Money Price(TicketQuery query)
    {
        Money result = _legacy.Price(query);
        _report.Record(Verify(query, result));
        return result;
    }

    private VerificationReport.IVerification Verify(TicketQuery query, Money legacyPrice)
    {
        try
        {
            Money candidatePrice = _candidate.Price(query);
            return legacyPrice.Equals(candidatePrice)
                ? new VerificationReport.Agreement(query, legacyPrice)
                : new VerificationReport.Divergence(query, legacyPrice, candidatePrice);
        }
        catch (Exception failure)
        {
            return new VerificationReport.CandidateFailure(query, legacyPrice,
                failure.GetType().Name + ": " + failure.Message);
        }
    }
}
