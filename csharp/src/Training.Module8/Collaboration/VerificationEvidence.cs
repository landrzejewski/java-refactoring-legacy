namespace Training.Module8.Collaboration;

public sealed record VerificationEvidence
{
    public VerificationEvidence(EvidenceKind kind, string observation)
    {
        ArgumentNullException.ThrowIfNull(observation);
        if (string.IsNullOrWhiteSpace(observation))
        {
            throw new ArgumentException("observation must not be blank");
        }
        Kind = kind;
        Observation = observation;
    }

    public EvidenceKind Kind { get; }

    public string Observation { get; }
}
