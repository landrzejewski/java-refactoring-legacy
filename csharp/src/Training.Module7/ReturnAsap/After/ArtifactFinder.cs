namespace Training.Module7.ReturnAsap.After;

public sealed class ArtifactFinder
{
    public Artifact? FindByChecksum(IReadOnlyList<Artifact> artifacts, string checksum)
    {
        ArgumentNullException.ThrowIfNull(artifacts);
        ArgumentNullException.ThrowIfNull(checksum);

        for (var index = 0; index < artifacts.Count; index++)
        {
            var artifact = artifacts[index]
                ?? throw new ArgumentNullException(nameof(artifacts), "artifact must not be null");
            if (checksum == artifact.Checksum)
            {
                return artifact;
            }
        }

        return null;
    }
}
