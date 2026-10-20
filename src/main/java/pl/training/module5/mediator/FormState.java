package pl.training.module5.mediator;

/**
 * Migawka formularza wydania - wspólna dla wersji przed i po refaktoryzacji.
 */
public record FormState(
        String version,
        boolean versionEditable,
        boolean approverEnabled,
        String approver,
        boolean publishEnabled,
        String status) {
}
