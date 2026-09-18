using static System.FormattableString;

namespace Training.Module7.Duplication.Before;

public sealed class LegacyArtifactPublisher
{
    public string PublishSnapshot(string artifactName, int buildNumber)
    {
        ArgumentNullException.ThrowIfNull(artifactName);
        if (string.IsNullOrWhiteSpace(artifactName))
        {
            throw new ArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0)
        {
            throw new ArgumentException("buildNumber must be greater than zero");
        }

        var normalizedName = artifactName.Trim().ToLowerInvariant();
        return Invariant($"{normalizedName}:{buildNumber}-SNAPSHOT");
    }

    public string PublishRelease(string artifactName, int buildNumber)
    {
        ArgumentNullException.ThrowIfNull(artifactName);
        if (string.IsNullOrWhiteSpace(artifactName))
        {
            throw new ArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0)
        {
            throw new ArgumentException("buildNumber must be greater than zero");
        }

        var normalizedName = artifactName.Trim().ToLowerInvariant();
        return Invariant($"{normalizedName}:{buildNumber}");
    }
}
