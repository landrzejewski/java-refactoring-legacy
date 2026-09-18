namespace Training.Module8.Incremental;

public interface IVerificationReporter
{
    void Report(VerificationEvent verificationEvent);

    static IVerificationReporter Ignoring() => Of(_ => { });

    /// <summary>Odpowiednik lambdy dla interfejsu funkcyjnego z Javy.</summary>
    static IVerificationReporter Of(Action<VerificationEvent> report)
    {
        ArgumentNullException.ThrowIfNull(report);
        return new DelegatingReporter(report);
    }

    private sealed class DelegatingReporter(
        Action<VerificationEvent> report) : IVerificationReporter
    {
        public void Report(VerificationEvent verificationEvent) =>
            report(verificationEvent);
    }
}
