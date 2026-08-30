package pl.training.module8.collaboration;

import java.util.Objects;

public record VerificationEvidence(EvidenceKind kind, String observation) {
    public VerificationEvidence {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(observation, "observation");
        if (observation.isBlank()) {
            throw new IllegalArgumentException("observation must not be blank");
        }
    }
}
