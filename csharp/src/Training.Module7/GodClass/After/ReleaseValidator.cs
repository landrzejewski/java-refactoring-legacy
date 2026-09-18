namespace Training.Module7.GodClass.After;

public sealed class ReleaseValidator
{
    public PublishedRelease Validate(string releaseId, string service, string version)
    {
        RequireText(releaseId, "releaseId");
        RequireText(service, "service");
        RequireText(version, "version");
        return new PublishedRelease(releaseId, service, version);
    }

    private static void RequireText(string? value, string field)
    {
        if (string.IsNullOrWhiteSpace(value))
        {
            throw new ArgumentException(field + " must not be blank");
        }
    }
}
