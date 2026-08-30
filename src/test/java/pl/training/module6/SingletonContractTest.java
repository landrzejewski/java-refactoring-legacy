package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Duration;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import pl.training.module6.singleton.DeploymentDefaults;
import pl.training.module6.singleton.before.LegacyDeploymentDefaults;

final class SingletonContractTest {
    @Test
    void enumLimitsInstantiationWithoutChangingTheDefaultValue() {
        var firstLegacy = new LegacyDeploymentDefaults();
        var secondLegacy = new LegacyDeploymentDefaults();

        assertNotSame(firstLegacy, secondLegacy);
        assertSame(
                DeploymentDefaults.INSTANCE,
                DeploymentDefaults.valueOf("INSTANCE"));
        assertEquals(
                firstLegacy.healthCheckTimeout(),
                DeploymentDefaults.INSTANCE.healthCheckTimeout());
        assertEquals(Duration.ofSeconds(30), firstLegacy.healthCheckTimeout());
    }

    @Test
    void allParallelAccessesObserveTheSameEnumConstant() {
        long identities = IntStream.range(0, 1_000)
                .parallel()
                .mapToObj(ignored -> DeploymentDefaults.INSTANCE)
                .distinct()
                .count();

        assertEquals(1, identities);
    }
}
