package pl.training.module5.facade.after;

import java.util.Objects;

public final class ReleaseCli {
    private final ReleasePublishingFacade publishing;

    public ReleaseCli(ReleasePublishingFacade publishing) {
        this.publishing = Objects.requireNonNull(publishing, "publishing");
    }

    public String publish(String releaseId, String environment) {
        return publishing.publish(releaseId, environment);
    }
}
