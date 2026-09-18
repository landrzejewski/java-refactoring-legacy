namespace Training.Module7.ReturnAsap.Before;

public sealed class LegacyArtifactFinder
{
    public Artifact? FindByChecksum(IReadOnlyList<Artifact> artifacts, string checksum)
    {
        ArgumentNullException.ThrowIfNull(artifacts);
        ArgumentNullException.ThrowIfNull(checksum);

        Artifact? result = null;
        var index = 0;
        while (result == null && index < artifacts.Count)
        {
            var artifact = artifacts[index]
                ?? throw new ArgumentNullException(nameof(artifacts), "artifact must not be null");
            if (checksum == artifact.Checksum)
            {
                result = artifact;
            }
            index++;
        }

        return result;
    }
}
