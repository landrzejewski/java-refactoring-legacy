package pl.training.module7.returnasap.after;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.training.module7.returnasap.Artifact;

public final class ArtifactFinder {
    public Optional<Artifact> findByChecksum(
            List<Artifact> artifacts, String checksum) {
        Objects.requireNonNull(artifacts, "artifacts must not be null");
        Objects.requireNonNull(checksum, "checksum must not be null");

        for (int index = 0; index < artifacts.size(); index++) {
            Artifact artifact = Objects.requireNonNull(
                    artifacts.get(index), "artifact must not be null");
            if (checksum.equals(artifact.checksum())) {
                return Optional.of(artifact);
            }
        }

        return Optional.empty();
    }
}
