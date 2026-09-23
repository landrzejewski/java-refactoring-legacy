package pl.training.workshop.m8.s07_adr;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Wykonywalny model decyzji z ADR-0007. Każda reguła ma identyfikator z ADR, więc naruszenie
 * w teście prowadzi wprost do uzasadnienia decyzji. Reguły działają na tekście źródeł
 * (świadome uproszczenie - patrz "Konsekwencje" w ADR).
 */
public final class ArchitectureRules {
    public enum Rule {
        R1_PRICING_WITHOUT_NOTIFICATION("ADR-0007/R1", Pattern.compile("^import .*\\.notification\\.")),
        R2_PRICING_USES_MONEY("ADR-0007/R2", Pattern.compile("\\b(double|Double)\\b"));

        private final String id;
        private final Pattern forbidden;

        Rule(String id, Pattern forbidden) {
            this.id = id;
            this.forbidden = forbidden;
        }

        public String id() {
            return id;
        }
    }

    private ArchitectureRules() {
    }

    /** Naruszenia w pakiecie pricing wariantu, np. "ADR-0007/R2 TicketPricing.java:12". */
    public static List<String> violations(Path variantDir) {
        List<String> violations = new ArrayList<>();
        for (Path file : javaFiles(variantDir.resolve("pricing"))) {
            List<String> lines = read(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).strip();
                if (isComment(line)) {
                    continue;
                }
                for (Rule rule : Rule.values()) {
                    if (rule.forbidden.matcher(line).find()) {
                        violations.add(rule.id() + " " + file.getFileName() + ":" + (i + 1));
                    }
                }
            }
        }
        return violations;
    }

    private static boolean isComment(String line) {
        return line.startsWith("//") || line.startsWith("/*") || line.startsWith("*");
    }

    private static List<Path> javaFiles(Path dir) {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(f -> f.toString().endsWith(".java")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> read(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
