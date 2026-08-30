package pl.training.module7.breakmethod;

public record ManifestEntry(
        String artifact,
        String checksum,
        int deploymentOrder) {
}
