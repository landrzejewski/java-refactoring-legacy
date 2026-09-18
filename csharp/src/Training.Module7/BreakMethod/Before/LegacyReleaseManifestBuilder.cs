using System.Text;
using static System.FormattableString;

namespace Training.Module7.BreakMethod.Before;

public sealed class LegacyReleaseManifestBuilder
{
    public string Build(IReadOnlyList<ManifestEntry> entries)
    {
        ArgumentNullException.ThrowIfNull(entries);

        var validatedEntries = new List<ManifestEntry>(entries.Count);
        foreach (var entry in entries)
        {
            if (entry is null)
            {
                throw new ArgumentNullException(nameof(entries), "entries must not contain null");
            }
            ArgumentNullException.ThrowIfNull(entry.Artifact, "artifact");
            if (string.IsNullOrWhiteSpace(entry.Artifact))
            {
                throw new ArgumentException("artifact must not be blank");
            }
            ArgumentNullException.ThrowIfNull(entry.Checksum, "checksum");
            if (string.IsNullOrWhiteSpace(entry.Checksum))
            {
                throw new ArgumentException("checksum must not be blank");
            }
            if (entry.DeploymentOrder < 0)
            {
                throw new ArgumentException("deploymentOrder must not be negative");
            }
            validatedEntries.Add(entry);
        }

        // List<T>.Sort is not stable, so a stable LINQ sort mirrors Java's List.sort.
        validatedEntries =
        [
            .. validatedEntries
                .OrderBy(entry => entry.DeploymentOrder)
                .ThenBy(entry => entry.Artifact, StringComparer.Ordinal)
        ];

        var manifest = new StringBuilder();
        foreach (var entry in validatedEntries)
        {
            if (manifest.Length > 0)
            {
                manifest.Append('\n');
            }
            manifest.Append(Invariant(
                $"{entry.DeploymentOrder}|{entry.Artifact}|{entry.Checksum}"));
        }
        return manifest.ToString();
    }
}
