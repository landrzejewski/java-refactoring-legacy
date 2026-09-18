using static System.FormattableString;

namespace Training.Module7.BreakMethod.After;

public sealed class ReleaseManifestBuilder
{
    public string Build(IReadOnlyList<ManifestEntry> entries)
    {
        var validatedEntries = ValidateAndCopy(entries);
        var orderedEntries = Order(validatedEntries);
        return Render(orderedEntries);
    }

    private static List<ManifestEntry> ValidateAndCopy(IReadOnlyList<ManifestEntry> entries)
    {
        ArgumentNullException.ThrowIfNull(entries);

        var copy = new List<ManifestEntry>(entries.Count);
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
            copy.Add(entry);
        }
        return copy;
    }

    // OrderBy/ThenBy is a stable sort, like Java's List.sort.
    private static List<ManifestEntry> Order(List<ManifestEntry> entries) =>
    [
        .. entries
            .OrderBy(entry => entry.DeploymentOrder)
            .ThenBy(entry => entry.Artifact, StringComparer.Ordinal)
    ];

    private static string Render(List<ManifestEntry> entries) =>
        string.Join(
            "\n",
            entries.Select(entry =>
                Invariant($"{entry.DeploymentOrder}|{entry.Artifact}|{entry.Checksum}")));
}
