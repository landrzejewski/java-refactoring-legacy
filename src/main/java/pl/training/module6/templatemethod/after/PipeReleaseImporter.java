package pl.training.module6.templatemethod.after;

public final class PipeReleaseImporter extends ReleaseImporter {
    @Override
    protected Fields parse(String raw) {
        String[] fields = raw.split("\\|", -1);
        if (fields.length != 2) {
            throw new IllegalArgumentException("expected releaseId and service");
        }
        return fields(fields[0].trim(), fields[1].trim());
    }
}
