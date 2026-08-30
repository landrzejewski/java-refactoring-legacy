package pl.training.module8.documentation;

import java.util.Objects;

public final class DecisionRecordMarkdownRenderer {
    public String render(DecisionRecord record) {
        Objects.requireNonNull(record, "record");

        var markdown = new StringBuilder();
        markdown.append("# ")
                .append(record.id())
                .append(": ")
                .append(record.title())
                .append("\n\n");
        section(markdown, "Status", statusLabel(record.status()));
        section(markdown, "Kontekst", record.context());
        section(markdown, "Decyzja", record.decision());

        markdown.append("## Rozważone opcje\n\n");
        for (int index = 0; index < record.consideredOptions().size(); index++) {
            DecisionOption option = record.consideredOptions().get(index);
            markdown.append(index + 1)
                    .append(". **")
                    .append(option.name())
                    .append("**: ")
                    .append(option.rationale())
                    .append('\n');
        }
        markdown.append('\n');

        markdown.append("## Konsekwencje\n\n");
        for (DecisionConsequence consequence : record.consequences()) {
            markdown.append("- **")
                    .append(consequenceLabel(consequence.kind()))
                    .append("**: ")
                    .append(consequence.description())
                    .append('\n');
        }
        markdown.append('\n');
        section(markdown, "Metoda weryfikacji", record.verificationMethod());
        return markdown.toString();
    }

    private static void section(
            StringBuilder markdown,
            String heading,
            String content) {

        markdown.append("## ")
                .append(heading)
                .append("\n\n")
                .append(content)
                .append("\n\n");
    }

    private static String statusLabel(DecisionStatus status) {
        return switch (status) {
            case PROPOSED -> "Proponowana";
            case ACCEPTED -> "Zaakceptowana";
            case REJECTED -> "Odrzucona";
            case DEPRECATED -> "Wycofana";
            case SUPERSEDED -> "Zastąpiona";
        };
    }

    private static String consequenceLabel(ConsequenceKind kind) {
        return switch (kind) {
            case POSITIVE -> "Pozytywna";
            case NEGATIVE -> "Negatywna";
            case NEUTRAL -> "Neutralna";
        };
    }
}
