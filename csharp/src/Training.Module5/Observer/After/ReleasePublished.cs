namespace Training.Module5.Observer.After;

public sealed record ReleasePublished
{
    public ReleasePublished(string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        ReleaseId = releaseId;
    }

    public string ReleaseId { get; }
}
