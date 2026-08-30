package pl.training.module6.typecode.after;

import java.util.Locale;
import java.util.Map;

public final class DeploymentZone {
    public static final DeploymentZone TEST = new DeploymentZone("TEST", false);
    public static final DeploymentZone PRODUCTION = new DeploymentZone("PROD", true);
    public static final DeploymentZone DISASTER_RECOVERY = new DeploymentZone("DR", true);

    private static final Map<String, DeploymentZone> BY_CODE = Map.of(
            TEST.code, TEST,
            PRODUCTION.code, PRODUCTION,
            DISASTER_RECOVERY.code, DISASTER_RECOVERY);

    private final String code;
    private final boolean approvalRequired;

    private DeploymentZone(String code, boolean approvalRequired) {
        this.code = code;
        this.approvalRequired = approvalRequired;
    }

    public static DeploymentZone fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("zoneCode must not be blank");
        }
        String normalized = code.toUpperCase(Locale.ROOT);
        DeploymentZone zone = BY_CODE.get(normalized);
        if (zone == null) {
            throw new IllegalArgumentException("unknown zone code: " + normalized);
        }
        return zone;
    }

    public String code() {
        return code;
    }

    public boolean requiresApproval() {
        return approvalRequired;
    }

    @Override
    public String toString() {
        return code;
    }
}
