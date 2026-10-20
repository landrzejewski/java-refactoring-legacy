package pl.training.module5.facade.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ReleaseScheduler {
    private final ReleasePublishingFacade publishing;

    public ReleaseScheduler(ReleasePublishingFacade publishing) {
        this.publishing = Objects.requireNonNull(publishing, "publishing");
    }

    public List<String> publishNightly(List<String> releaseIds) {
        List<String> published = new ArrayList<>();
        for (String releaseId : releaseIds) {
            published.add(publishing.publish(releaseId, "staging"));
        }
        return published;
    }
}
