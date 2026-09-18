namespace Training.Module6.TemplateMethod.After;

public abstract class ReleaseImporter
{
    // Template method: deliberately non-virtual (Java: final), so subclasses
    // can only customize the Parse step, never the order of the algorithm.
    public ReleaseDraft ImportRelease(string? raw)
    {
        if (string.IsNullOrWhiteSpace(raw))
        {
            throw new ArgumentException("input must not be blank");
        }

        var fields = Parse(raw);
        var releaseId = fields.ReleaseId;
        var service = fields.Service;
        if (string.IsNullOrWhiteSpace(releaseId) || string.IsNullOrWhiteSpace(service))
        {
            throw new ArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }

    protected abstract Fields Parse(string raw);

    // Java: fields(...); renamed because C# forbids a member named like the nested type.
    protected Fields CreateFields(string? releaseId, string? service) =>
        new(releaseId, service);

    protected sealed record Fields(string? ReleaseId, string? Service);
}
