namespace Training.Module6.Adapter.After;

public sealed record ReleaseMessage
{
    public ReleaseMessage(string? recipient, string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(recipient))
        {
            throw new ArgumentException("recipient must not be blank");
        }
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        Recipient = recipient;
        ReleaseId = releaseId;
    }

    public string Recipient { get; }

    public string ReleaseId { get; }
}
