namespace Training.Module7.BreakMethod;

public sealed record ManifestEntry(
    string Artifact,
    string Checksum,
    int DeploymentOrder);
