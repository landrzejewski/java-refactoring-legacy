package pl.training.module7.godclass.after;

import java.util.List;

import pl.training.module7.godclass.PublishedRelease;

public interface ReleaseRepository {
    boolean existsById(String releaseId);

    void save(PublishedRelease release);

    List<PublishedRelease> findAll();
}
