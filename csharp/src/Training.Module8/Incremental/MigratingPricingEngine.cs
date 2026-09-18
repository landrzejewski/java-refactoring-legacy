namespace Training.Module8.Incremental;

public sealed class MigratingPricingEngine : IPricingEngine
{
    private readonly IPricingEngine legacy;
    private readonly IPricingEngine candidate;
    private readonly IVerificationReporter reporter;
    private readonly MigrationMode mode;

    public MigratingPricingEngine(
        IPricingEngine legacy,
        IPricingEngine candidate,
        IVerificationReporter reporter,
        MigrationMode mode)
    {
        ArgumentNullException.ThrowIfNull(legacy);
        ArgumentNullException.ThrowIfNull(candidate);
        ArgumentNullException.ThrowIfNull(reporter);
        if (!Enum.IsDefined(mode))
        {
            throw new ArgumentOutOfRangeException(nameof(mode), mode, null);
        }
        this.legacy = legacy;
        this.candidate = candidate;
        this.reporter = reporter;
        this.mode = mode;
    }

    public PriceQuote Quote(PriceRequest request)
    {
        ArgumentNullException.ThrowIfNull(request);
        return mode switch
        {
            MigrationMode.Legacy => RequireQuote(
                legacy.Quote(request), "legacy quote"),
            MigrationMode.Verify => Verify(request),
            MigrationMode.Candidate => RequireQuote(
                candidate.Quote(request), "candidate quote"),
            _ => throw new InvalidOperationException($"Unknown mode {mode}")
        };
    }

    private PriceQuote Verify(PriceRequest request)
    {
        PriceQuote legacyQuote = RequireQuote(
            legacy.Quote(request), "legacy quote");
        try
        {
            PriceQuote candidateQuote = RequireQuote(
                candidate.Quote(request), "candidate quote");
            VerificationEvent verificationEvent = legacyQuote.Equals(candidateQuote)
                ? new VerificationEvent.Agreement(request, legacyQuote)
                : new VerificationEvent.Divergence(
                    request, legacyQuote, candidateQuote);
            TryToReport(verificationEvent);
        }
        catch (Exception failure) when (!IsCritical(failure))
        {
            TryToReport(
                new VerificationEvent.CandidateFailure(
                    request,
                    legacyQuote,
                    failure.GetType().FullName ?? failure.GetType().Name,
                    failure.Message));
        }
        return legacyQuote;
    }

    private void TryToReport(VerificationEvent verificationEvent)
    {
        try
        {
            reporter.Report(verificationEvent);
        }
        catch (Exception failure) when (!IsCritical(failure))
        {
            // Awaria reportera nie może zastąpić wyniku legacy.
        }
    }

    /// <summary>
    /// Odpowiednik rozróżnienia RuntimeException/Error z Javy: błędów
    /// krytycznych środowiska uruchomieniowego nie maskujemy.
    /// </summary>
    private static bool IsCritical(Exception failure) =>
        failure is OutOfMemoryException
            or InsufficientExecutionStackException
            or AccessViolationException;

    private static PriceQuote RequireQuote(
        PriceQuote? quote,
        string message) =>
        quote ?? throw new InvalidOperationException(message);
}
