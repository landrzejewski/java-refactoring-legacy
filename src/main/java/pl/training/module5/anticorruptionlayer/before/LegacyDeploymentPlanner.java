package pl.training.module5.anticorruptionlayer.before;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import pl.training.module5.anticorruptionlayer.LegacyCmdbClient;
import pl.training.module5.anticorruptionlayer.LegacyCmdbRecord;

/**
 * Logika domenowa mówi językiem CMDB: porównuje kody {@code "P"}, flagi {@code 1}
 * i parsuje daty. Każda zmiana w modelu CMDB przecieka do reguł planowania.
 */
public final class LegacyDeploymentPlanner {
    private static final DateTimeFormatter CMDB_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final LegacyCmdbClient cmdb;

    public LegacyDeploymentPlanner(LegacyCmdbClient cmdb) {
        this.cmdb = Objects.requireNonNull(cmdb, "cmdb");
    }

    public List<String> canaryTargets() {
        return cmdb.fetchAll().stream()
                .filter(record -> record.statFlg() == 1)
                .filter(record -> "T".equals(record.envCd()) || "D".equals(record.envCd()))
                .map(LegacyCmdbRecord::nm)
                .sorted()
                .toList();
    }

    public List<String> staleEnvironments(LocalDate today) {
        LocalDate threshold = today.minusDays(30);
        return cmdb.fetchAll().stream()
                .filter(record -> record.statFlg() == 1)
                .filter(record -> LocalDate.parse(record.lstDplyDt(), CMDB_DATE).isBefore(threshold))
                .map(LegacyCmdbRecord::nm)
                .sorted()
                .toList();
    }

    public boolean isProduction(String name) {
        return cmdb.fetchAll().stream()
                .anyMatch(record -> record.nm().equals(name) && "P".equals(record.envCd()));
    }
}
