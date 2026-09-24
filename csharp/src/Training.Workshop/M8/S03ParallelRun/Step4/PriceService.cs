using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Step4;

/// <summary>
/// Krok 4: przełączenie. Wybór ścieżki w jednym miejscu; Candidate czyni nowy kalkulator
/// autorytatywnym, a Legacy pozostaje natychmiastowym wycofaniem aż do usunięcia starego kodu.
/// </summary>
public sealed class PriceService
{
    private readonly LegacyPriceCalculator _legacy = new();
    private readonly CandidatePriceCalculator _candidate = new();
    private readonly MigrationMode _mode;
    private readonly VerificationReport _report;

    public PriceService()
        : this(MigrationMode.Shadow, new VerificationReport())
    {
    }

    public PriceService(MigrationMode mode, VerificationReport report)
    {
        _mode = mode;
        _report = report;
    }

    public Money Price(TicketQuery query)
    {
        return _mode switch
        {
            MigrationMode.Legacy => _legacy.Price(query),
            MigrationMode.Shadow => Shadow(query),
            MigrationMode.Candidate => _candidate.Price(query),
            _ => throw new InvalidOperationException("nieznany tryb: " + _mode),
        };
    }

    private Money Shadow(TicketQuery query)
    {
        Money result = _legacy.Price(query);
        try
        {
            Money candidatePrice = _candidate.Price(query);
            _report.Record(result.Equals(candidatePrice)
                ? new VerificationReport.Agreement(query, result)
                : new VerificationReport.Divergence(query, result, candidatePrice));
        }
        catch (Exception failure)
        {
            _report.Record(new VerificationReport.CandidateFailure(query, result,
                failure.GetType().Name + ": " + failure.Message));
        }
        return result;
    }
}
