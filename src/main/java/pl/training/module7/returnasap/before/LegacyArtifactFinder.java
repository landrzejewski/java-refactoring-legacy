package pl.training.module7.returnasap.before;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.training.module7.returnasap.Artifact;

public final class LegacyArtifactFinder {
    public Optional<Artifact> findByChecksum(
            List<Artifact> artifacts, String checksum) {
        Objects.requireNonNull(artifacts, "artifacts must not be null");
        Objects.requireNonNull(checksum, "checksum must not be null");

        Artifact result = null;
        int index = 0;
        while (result == null && index < artifacts.size()) {
            Artifact artifact = Objects.requireNonNull(
                    artifacts.get(index), "artifact must not be null");
            if (checksum.equals(artifact.checksum())) {
                result = artifact;
            }
            index++;
        }

        return Optional.ofNullable(result);
    }
}
