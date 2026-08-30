package pl.training.module6.typecode.before;

import java.util.Locale;

public record LegacyDeploymentRequest(String releaseId, String zoneCode) {
    public LegacyDeploymentRequest {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        if (zoneCode == null || zoneCode.isBlank()) {
            throw new IllegalArgumentException("zoneCode must not be blank");
        }
        zoneCode = zoneCode.toUpperCase(Locale.ROOT);
        if (!zoneCode.equals("TEST") && !zoneCode.equals("PROD") && !zoneCode.equals("DR")) {
            throw new IllegalArgumentException("unknown zone code: " + zoneCode);
        }
    }

    public boolean requiresApproval() {
        return zoneCode.equals("PROD") || zoneCode.equals("DR");
    }
}
