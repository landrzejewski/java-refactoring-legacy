package pl.training.workshop.m8.s10_qualitygate.step1;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import pl.training.workshop.m8.s10_qualitygate.GateInput;

/**
 * Krok 1: pierwszy wykonywalny punkt listy - skan źródeł domeny: znaczniki TODO/FIXME
 * i wydruki System.out/System.err. Tanie, deterministyczne, z plikiem i linią.
 */
public final class QualityGate {
    /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
    public List<String> evaluate(GateInput input) {
        List<String> findings = new ArrayList<>();
        scanSources(input, findings);
        return findings;
    }

    public boolean passes(GateInput input) {
        return evaluate(input).isEmpty();
    }

    /** TODO/FIXME i wydruki na konsolę w źródłach domeny. */
    private static void scanSources(GateInput input, List<String> findings) {
        for (Path file : javaFiles(input.sources())) {
            List<String> lines = read(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String where = file.getFileName() + ":" + (i + 1);
                if (line.contains("TODO") || line.contains("FIXME")) {
                    findings.add("TODO " + where);
                }
                if (line.contains("System.out") || line.contains("System.err")) {
                    findings.add("System.out " + where);
                }
            }
        }
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
