using static System.FormattableString;

namespace Training.Module7.Duplication.After;

public sealed class ArtifactPublisher
{
    public string PublishSnapshot(string artifactName, int buildNumber) =>
        Publish(artifactName, buildNumber, "-SNAPSHOT");

    public string PublishRelease(string artifactName, int buildNumber) =>
        Publish(artifactName, buildNumber, "");

    private static string Publish(string artifactName, int buildNumber, string qualifier)
    {
        var normalizedName = ValidateAndNormalize(artifactName, buildNumber);
        return Invariant($"{normalizedName}:{buildNumber}{qualifier}");
    }

    private static string ValidateAndNormalize(string artifactName, int buildNumber)
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

        return artifactName.Trim().ToLowerInvariant();
    }
}
