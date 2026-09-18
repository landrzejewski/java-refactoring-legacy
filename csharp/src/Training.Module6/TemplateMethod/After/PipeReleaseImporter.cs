namespace Training.Module6.TemplateMethod.After;

public sealed class PipeReleaseImporter : ReleaseImporter
{
    protected override Fields Parse(string raw)
    {
        var fields = raw.Split('|');
        if (fields.Length != 2)
        {
            throw new ArgumentException("expected releaseId and service");
        }
        return CreateFields(fields[0].Trim(), fields[1].Trim());
    }
}
