using Training.Module8.Incremental;

namespace Training.Module8.Tests.Incremental;

public sealed class MigratingPricingEngineTest
{
    private static readonly PriceRequest Request = new(100.00m, 1, 0m);
    private static readonly PriceQuote LegacyQuote = new(100.00m);
    private static readonly PriceQuote CandidateQuote = new(99.00m);

    [Fact]
    public void LegacyModeCallsOnlyTheLegacyImplementation()
    {
        var legacyCalls = new CallCounter();
        var candidateCalls = new CallCounter();
        var events = new List<VerificationEvent>();
        IPricingEngine engine = Engine(
            Returning(legacyCalls, LegacyQuote),
            Returning(candidateCalls, CandidateQuote),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Legacy);

        PriceQuote result = engine.Quote(Request);

        Assert.Equal(LegacyQuote, result);
        Assert.Equal(1, legacyCalls.Count);
        Assert.Equal(0, candidateCalls.Count);
        Assert.Empty(events);
    }

    [Fact]
    public void CandidateModeCallsOnlyTheCandidateImplementation()
    {
        var legacyCalls = new CallCounter();
        var candidateCalls = new CallCounter();
        var events = new List<VerificationEvent>();
        IPricingEngine engine = Engine(
            Returning(legacyCalls, LegacyQuote),
            Returning(candidateCalls, CandidateQuote),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Candidate);

        PriceQuote result = engine.Quote(Request);

        Assert.Equal(CandidateQuote, result);
        Assert.Equal(0, legacyCalls.Count);
        Assert.Equal(1, candidateCalls.Count);
        Assert.Empty(events);
    }

    [Fact]
    public void VerifyModeReportsAgreementAndReturnsTheLegacyResult()
    {
        var legacyCalls = new CallCounter();
        var candidateCalls = new CallCounter();
        var events = new List<VerificationEvent>();
        IPricingEngine engine = Engine(
            Returning(legacyCalls, LegacyQuote),
            Returning(candidateCalls, LegacyQuote),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        PriceQuote result = engine.Quote(Request);

        Assert.Same(LegacyQuote, result);
        Assert.Equal(1, legacyCalls.Count);
        Assert.Equal(1, candidateCalls.Count);
        var agreement = Assert.IsType<VerificationEvent.Agreement>(events[0]);
        Assert.Equal(Request, agreement.Request);
        Assert.Equal(LegacyQuote, agreement.Quote);
    }

    [Fact]
    public void VerifyModeReportsDivergenceButStillReturnsTheLegacyResult()
    {
        var events = new List<VerificationEvent>();
        IPricingEngine engine = Engine(
            IPricingEngine.Of(_ => LegacyQuote),
            IPricingEngine.Of(_ => CandidateQuote),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        PriceQuote result = engine.Quote(Request);

        Assert.Same(LegacyQuote, result);
        var divergence = Assert.IsType<VerificationEvent.Divergence>(events[0]);
        Assert.Equal(Request, divergence.Request);
        Assert.Equal(LegacyQuote, divergence.LegacyQuote);
        Assert.Equal(CandidateQuote, divergence.CandidateQuote);
    }

    [Fact]
    public void CandidateFailureCannotChangeTheVerifyResponse()
    {
        var events = new List<VerificationEvent>();
        IPricingEngine engine = Engine(
            IPricingEngine.Of(_ => LegacyQuote),
            IPricingEngine.Of(_ =>
                throw new InvalidOperationException("candidate unavailable")),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        PriceQuote result = engine.Quote(Request);

        Assert.Same(LegacyQuote, result);
        var failure = Assert.IsType<VerificationEvent.CandidateFailure>(events[0]);
        Assert.Equal(Request, failure.Request);
        Assert.Equal(LegacyQuote, failure.LegacyQuote);
        Assert.Equal("System.InvalidOperationException", failure.ExceptionType);
        Assert.Equal("candidate unavailable", failure.Message);
    }

    [Fact]
    public void JvmErrorsAreNotMaskedByVerification()
    {
        // Odpowiednik java.lang.Error w .NET: krytyczne wyjątki środowiska
        // uruchomieniowego (tu OutOfMemoryException) nie są maskowane.
        var candidateFailure = new OutOfMemoryException("candidate corrupted");
        IPricingEngine engine = Engine(
            IPricingEngine.Of(_ => LegacyQuote),
            IPricingEngine.Of(_ => throw candidateFailure),
            IVerificationReporter.Ignoring(),
            MigrationMode.Verify);

        var result = Assert.Throws<OutOfMemoryException>(
            () => engine.Quote(Request));

        Assert.Same(candidateFailure, result);
    }

    [Fact]
    public void InvalidCandidateResultIsReportedAsFailure()
    {
        var events = new List<VerificationEvent>();
        IPricingEngine engine = Engine(
            IPricingEngine.Of(_ => LegacyQuote),
            IPricingEngine.Of(_ => null!),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        PriceQuote result = engine.Quote(Request);

        Assert.Same(LegacyQuote, result);
        var failure = Assert.IsType<VerificationEvent.CandidateFailure>(events[0]);
        Assert.Equal("System.InvalidOperationException", failure.ExceptionType);
        Assert.Equal("candidate quote", failure.Message);
    }

    [Fact]
    public void ReporterFailureCannotChangeTheVerifyResponse()
    {
        IPricingEngine engine = Engine(
            IPricingEngine.Of(_ => LegacyQuote),
            IPricingEngine.Of(_ => LegacyQuote),
            IVerificationReporter.Of(_ =>
                throw new InvalidOperationException("reporter unavailable")),
            MigrationMode.Verify);

        Assert.Same(LegacyQuote, engine.Quote(Request));
    }

    [Fact]
    public void LegacyFailureRemainsAuthoritativeAndSkipsTheCandidate()
    {
        var candidateCalls = new CallCounter();
        var events = new List<VerificationEvent>();
        var legacyFailure = new InvalidOperationException("legacy unavailable");
        IPricingEngine engine = Engine(
            IPricingEngine.Of(_ => throw legacyFailure),
            Returning(candidateCalls, CandidateQuote),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        var result = Assert.Throws<InvalidOperationException>(
            () => engine.Quote(Request));

        Assert.Same(legacyFailure, result);
        Assert.Equal(0, candidateCalls.Count);
        Assert.Empty(events);
    }

    [Fact]
    public void MissingRequestNeverReachesEitherImplementation()
    {
        var legacyCalls = new CallCounter();
        var candidateCalls = new CallCounter();
        IPricingEngine engine = Engine(
            Returning(legacyCalls, LegacyQuote),
            Returning(candidateCalls, CandidateQuote),
            IVerificationReporter.Ignoring(),
            MigrationMode.Verify);

        var failure = Assert.Throws<ArgumentNullException>(
            () => engine.Quote(null!));

        Assert.Equal("request", failure.ParamName);
        Assert.Equal(0, legacyCalls.Count);
        Assert.Equal(0, candidateCalls.Count);
    }

    [Fact]
    public void ConstructorRejectsEveryMissingDependency()
    {
        IPricingEngine validEngine = IPricingEngine.Of(_ => LegacyQuote);
        IVerificationReporter validReporter = IVerificationReporter.Ignoring();

        AssertMissing("legacy", () => Engine(
            null!, validEngine, validReporter, MigrationMode.Legacy));
        AssertMissing("candidate", () => Engine(
            validEngine, null!, validReporter, MigrationMode.Legacy));
        AssertMissing("reporter", () => Engine(
            validEngine, validEngine, null!, MigrationMode.Legacy));
        // Enum w C# nie może być null — odpowiednikiem jest wartość spoza zakresu.
        var undefinedMode = Assert.Throws<ArgumentOutOfRangeException>(() => Engine(
            validEngine, validEngine, validReporter, (MigrationMode)42));
        Assert.Equal("mode", undefinedMode.ParamName);
    }

    private static MigratingPricingEngine Engine(
        IPricingEngine legacy,
        IPricingEngine candidate,
        IVerificationReporter reporter,
        MigrationMode mode) =>
        new(legacy, candidate, reporter, mode);

    private static IPricingEngine Returning(
        CallCounter calls,
        PriceQuote result) =>
        IPricingEngine.Of(_ =>
        {
            calls.Increment();
            return result;
        });

    private static void AssertMissing(string parameterName, Action action)
    {
        var failure = Assert.Throws<ArgumentNullException>(action);
        Assert.Equal(parameterName, failure.ParamName);
    }

    /// <summary>Odpowiednik AtomicInteger z testu Javy.</summary>
    private sealed class CallCounter
    {
        private int count;

        public int Count => Volatile.Read(ref count);

        public void Increment() => Interlocked.Increment(ref count);
    }
}
