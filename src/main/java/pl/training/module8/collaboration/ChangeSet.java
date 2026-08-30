package pl.training.module8.collaboration;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record ChangeSet(
        String title,
        Set<ChangeIntent> intents,
        List<VerificationEvidence> verificationEvidence,
        boolean independentlyGreenBuild) {

    public ChangeSet {
        Objects.requireNonNull(title, "title");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        intents = Set.copyOf(Objects.requireNonNull(intents, "intents"));
        verificationEvidence = List.copyOf(Objects.requireNonNull(
                verificationEvidence,
                "verificationEvidence"));
    }
}
