package pl.training.module8.documentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

final class DecisionRecordTest {
    @Test
    void snapshotsCollectionsAndExposesImmutableViews() {
        var options = new ArrayList<>(List.of(
                new DecisionOption("Port", "Separates the domain from time access")));
        var consequences = new ArrayList<>(List.of(
                new DecisionConsequence(
                        ConsequenceKind.POSITIVE,
                        "Tests can supply a deterministic clock")));

        DecisionRecord record = record(options, consequences);
        options.clear();
        consequences.clear();

        assertEquals(1, record.consideredOptions().size());
        assertEquals(1, record.consequences().size());
        assertThrows(
                UnsupportedOperationException.class,
                () -> record.consideredOptions().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> record.consequences().clear());
    }

    @Test
    void rejectsInvalidIdentifiersAndIncompleteRecords() {
        assertThrows(NullPointerException.class, () -> new DecisionId(null));
        assertThrows(IllegalArgumentException.class, () -> new DecisionId("42"));

        assertThrows(
                IllegalArgumentException.class,
                () -> new DecisionRecord(
                        new DecisionId("ADR-0042"),
                        " ",
                        DecisionStatus.PROPOSED,
                        "Context",
                        "Decision",
                        List.of(new DecisionOption("Option", "Rationale")),
                        List.of(new DecisionConsequence(
                                ConsequenceKind.NEUTRAL,
                                "Consequence")),
                        "Run tests"));
        assertThrows(
                IllegalArgumentException.class,
                () -> record(List.of(), List.of(new DecisionConsequence(
                        ConsequenceKind.NEUTRAL,
                        "Consequence"))));
        assertThrows(
                IllegalArgumentException.class,
                () -> record(List.of(new DecisionOption("Option", "Rationale")),
                        List.of()));
        assertThrows(
                IllegalArgumentException.class,
                () -> new DecisionOption("Option", " "));
        assertThrows(
                IllegalArgumentException.class,
                () -> new DecisionConsequence(ConsequenceKind.POSITIVE, " "));
    }

    private static DecisionRecord record(
            List<DecisionOption> options,
            List<DecisionConsequence> consequences) {

        return new DecisionRecord(
                new DecisionId("ADR-0042"),
                "Use a clock port",
                DecisionStatus.ACCEPTED,
                "The domain reads system time directly.",
                "Introduce a clock port at the application boundary.",
                options,
                consequences,
                "Run characterization tests before and after the change.");
    }
}
