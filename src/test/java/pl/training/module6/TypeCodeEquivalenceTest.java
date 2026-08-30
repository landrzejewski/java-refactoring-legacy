package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module6.typecode.after.DeploymentRequest;
import pl.training.module6.typecode.after.DeploymentZone;
import pl.training.module6.typecode.before.LegacyDeploymentRequest;

final class TypeCodeEquivalenceTest {
    @Test
    void preservesMeaningOfEveryKnownCode() {
        for (String code : new String[] {"TEST", "PROD", "DR", "prod"}) {
            var before = new LegacyDeploymentRequest("rel-42", code);
            var after = new DeploymentRequest(
                    "rel-42", DeploymentZone.fromCode(code));

            assertEquals(before.requiresApproval(), after.requiresApproval());
            assertEquals(before.zoneCode(), after.zone().code());
        }
    }

    @Test
    void canonicalizesKnownInstancesAndRejectsUnknownCodes() {
        assertSame(DeploymentZone.PRODUCTION, DeploymentZone.fromCode("prod"));
        assertEquals(
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new LegacyDeploymentRequest("rel-42", null))
                        .getMessage(),
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DeploymentZone.fromCode(null))
                        .getMessage());
        assertEquals(
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new LegacyDeploymentRequest("rel-42", "unknown"))
                        .getMessage(),
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DeploymentZone.fromCode("unknown"))
                        .getMessage());
    }
}
