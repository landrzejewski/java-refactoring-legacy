namespace Training.Module6.TemplateMethod.Before;

public sealed class LegacyKeyValueReleaseImporter
{
    public ReleaseDraft ImportRelease(string? raw)
    {
        if (string.IsNullOrWhiteSpace(raw))
        {
            throw new ArgumentException("input must not be blank");
        }

        string? releaseId = null;
        string? service = null;
        foreach (var field in raw.Split(';'))
        {
            var pair = field.Split('=', 2);
            if (pair.Length != 2)
            {
                throw new ArgumentException("expected key=value");
            }
            switch (pair[0].Trim())
            {
                case "id":
                    releaseId = pair[1].Trim();
                    break;
                case "service":
                    service = pair[1].Trim();
                    break;
                default:
                    throw new ArgumentException("unknown field: " + pair[0]);
            }
        }

        if (string.IsNullOrWhiteSpace(releaseId) || string.IsNullOrWhiteSpace(service))
        {
            throw new ArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }
}
