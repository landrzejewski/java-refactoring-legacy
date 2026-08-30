package pl.training.module7.methodobject;

import java.util.Objects;

public record RiskAssessment(int score, RiskLevel level) {
    public RiskAssessment {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "score must be between 0 and 100");
        }
        Objects.requireNonNull(level, "level");
    }
}
