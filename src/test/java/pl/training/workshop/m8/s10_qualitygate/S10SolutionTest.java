package pl.training.workshop.m8.s10_qualitygate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Bramka budowana krok po kroku na próbkach domeny. Ścieżki względem katalogu roboczego =
 * katalog główny repozytorium (mvn test, isolated-test.sh).
 */
final class S10SolutionTest {
    private static final String PACKAGE = "pl/training/workshop/m8/s10_qualitygate/sample/";

    static final GateInput DIRTY = input("dirty", "dirty");
    static final GateInput CLEAN = input("clean", "clean");
    static final GateInput FAILING_TESTS = input("clean", "broken");

    private static final List<String> SCAN = List.of("TODO PriceTable.java:14", "System.out PriceTable.java:23");
    private static final List<String> COMPILER = List.of(
            "kompilator PriceTable.java:11 compiler.warn.raw.class.use",
            "kompilator PriceTable.java:11 compiler.warn.raw.class.use");
    private static final List<String> COVERAGE = List.of(
            "pokrycie PriceTable.vipSurcharge bez testu", "pokrycie PriceTable.lookupCount bez testu");

    @Test
    void startPassesEverythingBecauseItChecksNothing() {
        assertTrue(new pl.training.workshop.m8.s10_qualitygate.start.QualityGate().passes(DIRTY));
    }

    @Test
    void step1FindsTodoAndConsoleOutput() {
        assertEquals(SCAN, new pl.training.workshop.m8.s10_qualitygate.step1.QualityGate().evaluate(DIRTY));
    }

    @Test
    void step2AddsCompilerWarnings() {
        assertEquals(concat(COMPILER, SCAN),
                new pl.training.workshop.m8.s10_qualitygate.step2.QualityGate().evaluate(DIRTY));
    }

    @Test
    void step3AddsCoverageOfTheKeyClass() {
        assertEquals(concat(COMPILER, SCAN, COVERAGE),
                new pl.training.workshop.m8.s10_qualitygate.step3.QualityGate().evaluate(DIRTY));
    }

    @Test
    void step4AddsGreenTestsAndCatchesAFailingOne() {
        var gate = new pl.training.workshop.m8.s10_qualitygate.step4.QualityGate();
        assertEquals(concat(COMPILER, SCAN, COVERAGE), gate.evaluate(DIRTY), "testy próbki brudnej są zielone");
        assertEquals(List.of("test PriceTableTest.vipSurchargeStartsAtRowNine nie przechodzi: AssertionError"),
                gate.evaluate(FAILING_TESTS));
        assertFalse(gate.passes(FAILING_TESTS));
        assertTrue(gate.passes(CLEAN));
    }

    @Test
    void earlierGatesDoNotSeeFailingTests() {
        assertTrue(new pl.training.workshop.m8.s10_qualitygate.step3.QualityGate().passes(FAILING_TESTS));
    }

    private static GateInput input(String sources, String tests) {
        Path sourceDir = Path.of("src/main/java", PACKAGE, sources);
        Path testFile = Path.of("src/test/java", PACKAGE, tests, "PriceTableTest.java");
        if (!Files.isDirectory(sourceDir) || !Files.isRegularFile(testFile)) {
            throw new IllegalStateException("uruchom testy z katalogu głównego repozytorium, brak "
                    + sourceDir.toAbsolutePath() + " albo " + testFile);
        }
        return new GateInput(sourceDir, testFile, "PriceTable",
                "pl.training.workshop.m8.s10_qualitygate.sample." + tests + ".PriceTableTest");
    }

    @SafeVarargs
    private static List<String> concat(List<String>... parts) {
        return java.util.Arrays.stream(parts).flatMap(List::stream).toList();
    }
}
