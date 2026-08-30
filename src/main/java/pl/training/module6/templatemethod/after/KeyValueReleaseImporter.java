package pl.training.module6.templatemethod.after;

public final class KeyValueReleaseImporter extends ReleaseImporter {
    @Override
    protected Fields parse(String raw) {
        String releaseId = null;
        String service = null;
        for (String field : raw.split(";", -1)) {
            String[] pair = field.split("=", 2);
            if (pair.length != 2) {
                throw new IllegalArgumentException("expected key=value");
            }
            switch (pair[0].trim()) {
                case "id" -> releaseId = pair[1].trim();
                case "service" -> service = pair[1].trim();
                default -> throw new IllegalArgumentException("unknown field: " + pair[0]);
            }
        }
        return fields(releaseId, service);
    }
}
