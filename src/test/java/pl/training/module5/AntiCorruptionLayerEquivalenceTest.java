package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module5.anticorruptionlayer.LegacyCmdbClient;
import pl.training.module5.anticorruptionlayer.LegacyCmdbRecord;
import pl.training.module5.anticorruptionlayer.after.DeploymentPlanner;
import pl.training.module5.anticorruptionlayer.after.Environment;
import pl.training.module5.anticorruptionlayer.after.EnvironmentDirectory;
import pl.training.module5.anticorruptionlayer.after.acl.CmdbEnvironmentDirectory;
import pl.training.module5.anticorruptionlayer.before.LegacyDeploymentPlanner;

final class AntiCorruptionLayerEquivalenceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);

    private static final LegacyCmdbClient CMDB = () -> List.of(
            new LegacyCmdbRecord("prod-eu", "P", 1, "20261015"),
            new LegacyCmdbRecord("test-b", "T", 1, "20260801"),
            new LegacyCmdbRecord("test-a", "T", 1, "20261019"),
            new LegacyCmdbRecord("dev-1", "D", 1, "20260901"),
            new LegacyCmdbRecord("dev-old", "D", 0, "20250101"),
            new LegacyCmdbRecord("prod-us", "P", 9, "20240101"));

    @Test
    void planningRulesGiveTheSameAnswersThroughTheAcl() {
        var before = new LegacyDeploymentPlanner(CMDB);
        var after = new DeploymentPlanner(new CmdbEnvironmentDirectory(CMDB));

        assertEquals(before.canaryTargets(), after.canaryTargets());
        assertEquals(before.staleEnvironments(TODAY), after.staleEnvironments(TODAY));
        for (String name : List.of("prod-eu", "prod-us", "test-a", "unknown")) {
            assertEquals(before.isProduction(name), after.isProduction(name), name);
        }
        assertEquals(List.of("dev-1", "test-a", "test-b"), after.canaryTargets());
        assertEquals(List.of("dev-1", "test-b"), after.staleEnvironments(TODAY));
    }

    @Test
    void translatorMapsLegacyCodesToTheDomainModel() {
        List<Environment> environments = new CmdbEnvironmentDirectory(CMDB).all();

        assertEquals(
                new Environment("prod-eu", Environment.Tier.PRODUCTION, true, LocalDate.of(2026, 10, 15)),
                environments.getFirst());
        assertEquals(
                new Environment("prod-us", Environment.Tier.PRODUCTION, false, LocalDate.of(2024, 1, 1)),
                environments.getLast());
    }

    @Test
    void unknownLegacyDataIsRejectedInsideTheAcl() {
        var unknownCode = new CmdbEnvironmentDirectory(
                () -> List.of(new LegacyCmdbRecord("x", "Q", 1, "20260101")));
        var badDate = new CmdbEnvironmentDirectory(
                () -> List.of(new LegacyCmdbRecord("x", "T", 1, "2026-01-01")));

        assertEquals("unknown CMDB environment code: Q",
                assertThrows(IllegalArgumentException.class, unknownCode::all).getMessage());
        assertEquals("invalid CMDB date: 2026-01-01",
                assertThrows(IllegalArgumentException.class, badDate::all).getMessage());
    }

    @Test
    void domainCanBeTestedWithoutAnyLegacyType() {
        EnvironmentDirectory directory = () -> List.of(
                new Environment("qa", Environment.Tier.TEST, true, TODAY));

        assertEquals(List.of("qa"), new DeploymentPlanner(directory).canaryTargets());
        assertTrue(Arrays.stream(Environment.class.getRecordComponents())
                .map(RecordComponent::getType)
                .noneMatch(type -> type.getPackageName().endsWith("anticorruptionlayer")));
    }
}
