namespace Training.Module6.TemplateMethod.Before;

public sealed class LegacyPipeReleaseImporter
{
    public ReleaseDraft ImportRelease(string? raw)
    {
        if (string.IsNullOrWhiteSpace(raw))
        {
            throw new ArgumentException("input must not be blank");
        }

        var fields = raw.Split('|');
        if (fields.Length != 2)
        {
            throw new ArgumentException("expected releaseId and service");
        }

        var releaseId = fields[0].Trim();
        var service = fields[1].Trim();
        if (string.IsNullOrWhiteSpace(releaseId) || string.IsNullOrWhiteSpace(service))
        {
            throw new ArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }
}
