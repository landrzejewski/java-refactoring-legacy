package pl.training.module1;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

final class Module1ExamplesTest {
    @Test
    void runsAllModuleExamples() {
        assertDoesNotThrow(() -> Module1Examples.main(new String[0]));
    }
}
