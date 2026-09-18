namespace Training.Module6.TemplateMethod.After;

public sealed class KeyValueReleaseImporter : ReleaseImporter
{
    protected override Fields Parse(string raw)
    {
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
        return CreateFields(releaseId, service);
    }
}
