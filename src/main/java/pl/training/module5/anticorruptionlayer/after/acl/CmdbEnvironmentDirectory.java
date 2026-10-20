package pl.training.module5.anticorruptionlayer.after.acl;

import java.util.List;
import java.util.Objects;

import pl.training.module5.anticorruptionlayer.LegacyCmdbClient;
import pl.training.module5.anticorruptionlayer.after.Environment;
import pl.training.module5.anticorruptionlayer.after.EnvironmentDirectory;

/**
 * Anti-Corruption Layer: adapter implementujący port domenowy + tłumacz modelu.
 * Typy CMDB nie wychodzą poza ten pakiet.
 * <p>
 * Facade a ACL: fasada upraszcza interfejs podsystemu, ale zostaje w tym samym modelu
 * (te same pojęcia, mniej wywołań). ACL tłumaczy między dwoma różnymi modelami, aby
 * obcy model nie "zepsuł" języka domeny; często używa fasady lub adaptera w środku.
 */
public final class CmdbEnvironmentDirectory implements EnvironmentDirectory {
    private final LegacyCmdbClient cmdb;

    public CmdbEnvironmentDirectory(LegacyCmdbClient cmdb) {
        this.cmdb = Objects.requireNonNull(cmdb, "cmdb");
    }

    @Override
    public List<Environment> all() {
        return cmdb.fetchAll().stream()
                .map(CmdbTranslator::toEnvironment)
                .toList();
    }
}
